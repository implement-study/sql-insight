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

import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.core.bean.Row
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.Value
import tech.insight.engine.innodb.factory.RecordHeaderFactory
import tech.insight.engine.innodb.page.compact.RecordHeader

/**
 * max record in group
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class Supremum : InnodbUserRecord {

    companion object {
        const val SUPREMUM_BODY = "supremum"
    }

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

    override fun rowBytes(): ByteArray {
        return DynamicByteBuffer.wrap(recordHeader.toBytes()).append(body).toBytes()
    }

    override fun toString(): String {
        return recordHeader.toString() + "[body:" + String(body) + "]"
    }

    override val values: List<Value<*>>
        get() = supremumUnsupported()

    override val rowId: Long
        get() = Long.MAX_VALUE

    override fun getValueByColumnName(colName: String): Value<*> {
        supremumUnsupported()
    }

    override fun offset(): Int {
        return ConstantSize.SUPREMUM.offset()
    }

    override fun setOffset(offset: Int) {
        throw UnsupportedOperationException("supremum can't set offset ")
    }

    override operator fun compareTo(that: Row): Int {
        return if (that is InnodbUserRecord) {
            1
        } else supremumUnsupported()
    }

    override fun nextRecordOffset(): Int {
        return 0
    }

    override fun deleteSign(): Boolean {
        return false
    }

    override fun belongTo(): Table {
        supremumUnsupported()
    }

    fun setRecordHeader(recordHeader: RecordHeader): Supremum {
        this.recordHeader = recordHeader
        return this
    }

    private fun supremumUnsupported(): Nothing {
        throw UnsupportedOperationException("this is supremum!")
    }


    override fun beforeSplitOffset(): Int {
        return recordHeader.length()
    }

    override fun length(): Int {
        return ConstantSize.SUPREMUM.size()
    }


}
