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
package tech.insight.engine.innodb.page.compact

import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.core.bean.Index
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.Value
import tech.insight.engine.innodb.page.IndexNode
import tech.insight.engine.innodb.page.InnodbUserRecord

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class IndexRecord(override val recordHeader: RecordHeader, indexNode: IndexNode, index: Index) : InnodbUserRecord {
    private val index: Index
    private val indexNode: IndexNode
    private var offsetInPage = -1

    constructor(indexNode: IndexNode, index: Index) : this(RecordHeader.create(RecordType.PAGE), indexNode, index)

    init {
        this.index = index
        this.indexNode = indexNode
    }

    /**
     * a index record body is a index node
     */
    fun indexNode(): IndexNode {
        return indexNode
    }

    override val values: List<Value<*>>
        get() = indexNode.key.toList()

    override val rowId: Long
        get() = -1

    override fun getValueByColumnName(colName: String): Value<*> {
        throw UnsupportedOperationException()
    }

    override fun belongTo(): Table {
        return index.belongTo()
    }

    override fun beforeSplitOffset(): Int {
        return recordHeader.length()
    }

    override fun indexKey(): Array<Value<*>> {
        return this.indexNode.key
    }

    override fun deleteSign(): Boolean {
        return recordHeader.delete
    }

    override fun rowBytes(): ByteArray {
        val buffer: DynamicByteBuffer = DynamicByteBuffer.wrap(recordHeader.toBytes())
        buffer.append(indexNode.toBytes())
        return buffer.toBytes()
    }

    override fun offset(): Int {
        require(offsetInPage != -1) { "unknown offset" }
        return offsetInPage
    }

    override fun setOffset(offset: Int) {
        offsetInPage = offset
    }

    override fun nextRecordOffset(): Int {
        return recordHeader.nextRecordOffset
    }

    override fun length(): Int {
        return recordHeader.length() + indexNode.length()
    }
}
