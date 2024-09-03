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

import org.gongxuanzhang.easybyte.core.ByteWrapper
import tech.insight.buffer.readUShort
import tech.insight.core.annotation.Unused

/**
 * 56 bytes.
 * record page info that may change frequently
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class PageHeader(override val belongPage: InnoDbPage) : PageObject, ByteWrapper {
    //  todo field update adjust bytebuffer
    val source = belongPage.source.slice(ConstantSize.PAGE_HEADER.offset(), ConstantSize.PAGE_HEADER.size())

    /**
     * page slot count
     * 2 bytes
     */
    var slotCount: Int = source.readUShort().toInt()

    /**
     * offset of free space start
     * 2 bytes
     */
    var heapTop: Int = source.readUShort().toInt()

    /**
     * page record count include infimum and supremum and deleted record
     * 2 bytes
     */
    var absoluteRecordCount: Int = source.readUShort().toInt()

    /**
     * page record count exclude infimum and supremum and deleted record
     * 2 bytes
     */
    var recordCount: Int = source.readUShort().toInt()

    /**
     * the first deleted record in page. use next_record field can find delete linked list, init is 0
     * 2 bytes
     */
    var free: Int = source.readUShort().toInt()

    /**
     * deleted record occupy space
     * 2 bytes
     */
    var garbage: Int = source.readUShort().toInt()

    /**
     * last insert record offset,init is 0
     * 2 bytes
     */
    var lastInsertOffset: Int = source.readUShort().toInt()

    /**
     * insert direction that use for support insert.
     * 0 is left.
     * 1 is right.
     * 2 bytes
     */
    var direction: Int = source.readShort().toInt()

    /**
     * number of inserts in the same direction
     * 2 bytes
     */
    var directionCount: Int = source.readUShort().toInt()

    /**
     * the max transaction id in page
     */
    @Unused
    var maxTransactionId: Long = source.readLong()

    /**
     * this page in b-tree layer level
     * leaf node level is 0.
     * 2 bytes
     */
    var level: Int = source.readShort().toInt()

    /**
     * which index the page belong to
     */
    var indexId: Long = source.readLong()

    /**
     * join seg leaf 10 bytes
     */
    @Unused
    var segLeafPre: Short = source.readShort()

    /**
     * 10 bytes.
     * b-tree leaf-node header info . only root page have.
     */
    @Unused
    var segLeaf: Long = source.readLong()

    /**
     * join seg top 10 bytes
     */
    @Unused
    var segTopPre: Short = source.readShort()

    /**
     * 10 bytes.
     * b-tree non-leaf-node header info . only root page have.
     */
    var segTop: Long = source.readLong()

    override fun length(): Int {
        return ConstantSize.PAGE_HEADER.size()
    }

    override fun toBytes(): ByteArray {
        return source.array()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PageHeader
        return source == other.source
    }

    override fun hashCode(): Int {
        return source.hashCode()
    }

    /**
     * add a record page header will update
     */
    fun addRecord(userRecord: InnodbUserRecord) {
        this.absoluteRecordCount++
        this.recordCount++
        this.heapTop += userRecord.length()
    }

    companion object PageHeaderFactory {

        val EMPTY_PAGE_HEAP_TOP = ConstantSize.FILE_HEADER.size() +
                ConstantSize.PAGE_HEADER.size() +
                ConstantSize.INFIMUM.size() +
                ConstantSize.SUPREMUM.size()

        /**
         * create a empty page header
         */
        fun create(belongPage: InnoDbPage) = PageHeader(belongPage).apply {
            this.slotCount = 2
            this.heapTop = EMPTY_PAGE_HEAP_TOP
            this.absoluteRecordCount = 2
            this.recordCount = 0
            this.free = 0
            this.garbage = 0
            this.lastInsertOffset = EMPTY_PAGE_HEAP_TOP
            this.level = 0
            this.direction = 0
            this.directionCount = 0
            this.maxTransactionId = 0L
            this.indexId = 0
            this.segLeafPre = 0.toShort()
            this.segLeaf = 0L
            this.segTopPre = 0.toShort()
            this.segTop = 0L
        }

    }
}
