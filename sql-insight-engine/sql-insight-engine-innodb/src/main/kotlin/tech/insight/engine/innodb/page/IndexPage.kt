/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/sql-insight/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.insight.engine.innodb.page

import org.gongxuanzhang.sql.insight.core.`object`.Column
import tech.insight.engine.innodb.page.compact.IndexRecord
import java.nio.ByteBuffer

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class IndexPage(index: InnodbIndex?) : InnoDbPage(index) {
    override fun insertData(data: InnodbUserRecord) {
        if (data is IndexRecord) {
            super.insertData(data)
            return
        }
        val preAndNext = findPreAndNext(data)
        val pre = preAndNext!!.first
        val next = preAndNext.second
        val hit: IndexRecord
        hit = if (pre is Infimum) {
            next as IndexRecord
        } else {
            pre as IndexRecord
        }
        val pointPage: InnoDbPage = PageFactory.findPageByOffset(hit.indexNode().getPointer(), ext.belongIndex)
        pointPage.insertData(data)
    }

    override fun wrapUserRecord(offsetInPage: Int): IndexRecord {
        //  todo dynamic primary key
        val columns: List<Column> = ext.belongIndex.columns()
        val recordHeader: RecordHeader = RowFormatFactory.readRecordHeader(this, offsetInPage)
        val key: Array<Value?> = arrayOfNulls<Value>(columns.size)
        val buffer = ByteBuffer.wrap(toBytes(), offsetInPage, length() - offsetInPage)
        for (i in key.indices) {
            val column: Column = columns[i]
            val valueArr = ByteArray(column.getDataType().getLength())
            buffer[valueArr]
            key[i] = ValueNegotiator.wrapValue(column, valueArr)
        }
        return IndexRecord(recordHeader, IndexNode(key, buffer.getInt()), ext.belongIndex)
    }

    /**
     * data page will split when free space less than one in thirty-two page size
     */
    override fun splitIfNecessary() {
        if (this.freeSpace > ConstantSize.PAGE.size() shr 5) {
            return
        }
        val allRecords: MutableList<InnodbUserRecord?> = ArrayList(pageHeader!!.recordCount + 1)
        var base: InnodbUserRecord? = infimum
        while (true) {
            base = getUserRecordByOffset(base.offset() + base.nextRecordOffset())
            if (base === supremum) {
                break
            }
            allRecords.add(base)
        }
        val pre: InnoDbPage = PageFactory.createIndexPage(allRecords.subList(0, allRecords.size / 2), ext.belongIndex)
        val next: InnoDbPage = PageFactory.createIndexPage(
            allRecords.subList(allRecords.size / 2, allRecords.size),
            ext.belongIndex
        )
        upgrade(pre, next)
    }

    override fun pageIndex(): IndexRecord {
        val firstData: IndexRecord =
            getUserRecordByOffset(infimum!!.offset() + infimum!!.nextRecordOffset()) as IndexRecord
        val node = IndexNode(firstData.indexNode().getKey(), fileHeader!!.offset)
        return IndexRecord(node, ext.belongIndex)
    }

    override fun compare(o1: InnodbUserRecord, o2: InnodbUserRecord): Int {
        require(o1 is IndexRecord) { "index page only support compare index record" }
        val values1: Array<Value> = (o1 as IndexRecord).indexNode().getKey()
        val values2: Array<Value> = (o2 as IndexRecord).indexNode().getKey()
        for (i in values1.indices) {
            val compare: Int = values1[i].compareTo(values2[i])
            if (compare != 0) {
                return compare
            }
        }
        return 0
    }
}
