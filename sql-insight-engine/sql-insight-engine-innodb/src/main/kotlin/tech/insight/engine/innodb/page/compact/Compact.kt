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
package tech.insight.engine.innodb.page.compact

import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.core.annotation.Unused
import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.InnodbUserRecord


/**
 * @author gongxuanzhangmelt@gmail.com
 */
class Compact : InnodbUserRecord {
    /**
     * variable column list
     */
    var variables: Variables? = null

    /**
     * null list.
     * size is table nullable column count / 8.
     */
    var nullList: CompactNullList? = null

    /**
     * record header 5 bytes
     */
    var recordHeader: RecordHeader? = null

    /**
     * 真实记录
     */
    var body: ByteArray

    /**
     * 6字节  唯一标识
     */
    @Unused
    var rowId: Long = 0

    /**
     * 事务id  6字节
     */
    @Unused
    var transactionId: Long = 0

    /**
     * 7字节，回滚指针
     */
    @Unused
    var rollPointer: Long = 0
    var sourceRow: Row? = null
    var offsetInPage = -1
    fun rowBytes(): ByteArray {
        val buffer: DynamicByteBuffer = DynamicByteBuffer.allocate()
        buffer.append(variables!!.toBytes())
        buffer.append(nullList!!.toBytes())
        buffer.append(recordHeader!!.toBytes())
        buffer.append(body)
        return buffer.toBytes()
    }

    val values: List<Any>
        get() = sourceRow.getValues()

    fun getRowId(): Long {
        return sourceRow.getRowId()
    }

    fun getValueByColumnName(colName: String?): Value {
        return sourceRow.getValueByColumnName(colName)
    }

    fun belongTo(): Table {
        return sourceRow.belongTo()
    }

    override fun length(): Int {
        //    record header must write "ConstantSize.RECORD_HEADER.size()"
        //    because  the compact may come from insert row result in NullPointException
        return variables!!.length() + nullList!!.length() + ConstantSize.RECORD_HEADER.size() + body.size
    }

    override fun beforeSplitOffset(): Int {
        return variables!!.length() + nullList!!.length() + ConstantSize.RECORD_HEADER.size()
    }

    fun offset(): Int {
        require(offsetInPage != -1) { "unknown offset" }
        return offsetInPage
    }

    fun setOffset(offset: Int) {
        offsetInPage = offset
    }

    fun nextRecordOffset(): Int {
        return recordHeader.getNextRecordOffset()
    }

    fun deleteSign(): Boolean {
        return recordHeader.isDelete()
    }

    override fun toString(): String {
        return "Compact{" +
                "sourceRow=" + sourceRow +
                '}'
    }
}
