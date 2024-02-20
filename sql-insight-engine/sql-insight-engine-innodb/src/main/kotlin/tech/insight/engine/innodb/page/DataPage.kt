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

import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.PageFactory
import tech.insight.core.bean.Column
import tech.insight.engine.innodb.page.compact.IndexRecord


/**
 * @author gongxuanzhangmelt@gmail.com
 */
class DataPage(index: InnodbIndex) : InnoDbPage(index) {
    override fun wrapUserRecord(offsetInPage: Int): InnodbUserRecord {
        return RowFormatFactory.readRecordInPage(this, offsetInPage, ext.belongIndex.belongTo())
    }

    /**
     * data page will split when free space less than one-sixteenth page size
     */
    override fun splitIfNecessary() {
        if (this.freeSpace > ConstantSize.PAGE.size() shr 4) {
            return
        }
        val pageUserRecord: MutableList<InnodbUserRecord?> = ArrayList(
            pageHeader!!.recordCount + 1
        )
        var base: InnodbUserRecord? = infimum
        var allLength = 0
        while (true) {
            base = getUserRecordByOffset(base.offset() + base.nextRecordOffset())
            if (base === supremum) {
                break
            }
            pageUserRecord.add(base)
            allLength += base!!.length()
        }
        //   todo non middle split ?
        if (pageHeader!!.directionCount < Constant.Companion.DIRECTION_COUNT_THRESHOLD) {
            middleSplit(pageUserRecord, allLength)
        }
    }

    /**
     * middle split.
     * insert direction unidentified (directionCount less than 5)
     *
     *
     * if this page is root page.
     * transfer root page to index page from data page.
     * create two data page linked.
     * if this page is normal leaf node,
     * create a data page append to index file and insert a index record to parent (index page)
     *
     * @param pageUserRecord all user record in page with inserted
     * @param allLength      all user record length
     */
    private fun middleSplit(pageUserRecord: List<InnodbUserRecord?>, allLength: Int) {
        var allLength = allLength
        val half = allLength / 2
        var firstDataPage: DataPage? = null
        var secondDataPage: DataPage? = null
        for (i in pageUserRecord.indices) {
            allLength -= pageUserRecord[i]!!.length()
            if (allLength <= half) {
                val belong = ext.belongIndex
                firstDataPage = PageFactory.createDataPage(pageUserRecord.subList(0, i), belong)
                secondDataPage = PageFactory.createDataPage(pageUserRecord.subList(i, pageUserRecord.size), belong)
                break
            }
        }
        if (firstDataPage == null) {
            throw NullPointerException("data page error")
        }
        upgrade(firstDataPage, secondDataPage!!)
    }

    override fun pageIndex(): IndexRecord {
        val firstData = getUserRecordByOffset(infimum!!.offset() + infimum!!.nextRecordOffset())
        val columns: List<Column> = ext.belongIndex.columns()
        val values = columns.stream()
            .map<Any>(Column::getName)
            .map<Any>(firstData::getValueByColumnName)
            .toArray(IntFunction<Array<A>> { _Dummy_.__Array__() })
        return IndexRecord(IndexNode(values, fileHeader!!.offset), ext.belongIndex)
    }

    override fun compare(o1: InnodbUserRecord, o2: InnodbUserRecord): Int {
        return RowComparator.primaryKeyComparator().compare(o1, o2)
    }
}
