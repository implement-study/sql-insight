/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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

import lombok.EqualsAndHashCode
import tech.insight.engine.innodb.page.compact.RecordHeader

/**
 * max record in group
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
@EqualsAndHashCode
class Supremum : InnodbUserRecord {
    /**
     * 5 bytes
     */
    override var recordHeader: RecordHeader

    /**
     * 8 bytes as "supremum"
     */
    val body = SUPREMUM_BODY.toByteArray()

    init {
        recordHeader = RecordHeaderFactory.supremumHeader()
    }

    fun rowBytes(): ByteArray {
        return DynamicByteBuffer.wrap(recordHeader.toBytes()).append(body).toBytes()
    }

    override fun toString(): String {
        return recordHeader.toString() + "[body:" + String(body) + "]"
    }

    val values: List<Any>
        get() = supremumUnsupport<List<Value>>()
    val rowId: Long
        get() = Long.MAX_VALUE

    fun getValueByColumnName(colName: String?): Value {
        return supremumUnsupport()
    }

    fun offset(): Int {
        return ConstantSize.SUPREMUM.offset()
    }

    fun setOffset(offset: Int) {
        throw UnsupportedOperationException("supremum can't set offset ")
    }

    operator fun compareTo(that: Row): Int {
        return if (that is InnodbUserRecord) {
            1
        } else supremumUnsupport()
    }

    fun nextRecordOffset(): Int {
        return 0
    }

    fun deleteSign(): Boolean {
        return false
    }

    fun belongTo(): Table {
        return supremumUnsupport()
    }

    fun setRecordHeader(recordHeader: RecordHeader): Supremum {
        this.recordHeader = recordHeader
        return this
    }

    private fun <T> supremumUnsupport(): T {
        throw UnsupportedOperationException("this is supremum!")
    }

    override fun getRecordHeader(): RecordHeader {
        return recordHeader
    }

    override fun beforeSplitOffset(): Int {
        return recordHeader.length()
    }

    override fun length(): Int {
        return ConstantSize.SUPREMUM.size()
    }

    companion object {
        const val SUPREMUM_BODY = "supremum"
    }
}
