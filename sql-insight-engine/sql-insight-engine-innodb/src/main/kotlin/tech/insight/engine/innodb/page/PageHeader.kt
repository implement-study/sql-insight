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
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.core.annotation.Unused
import java.nio.ByteBuffer

/**
 * 56 bytes.
 * record page info that may change frequently
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class PageHeader private constructor() : PageObject, ByteWrapper {
    /**
     * page slot count
     */
    var slotCount: Short = 0

    /**
     * offset of free space start
     */
    var heapTop: Short = 0

    /**
     * page record count include infimum and supremum and deleted record
     */
    var absoluteRecordCount: Short = 0

    /**
     * page record count exclude infimum and supremum and deleted record
     */
    var recordCount: Short = 0

    /**
     * the first deleted record in page. use next_record field can find delete linked list, init is 0
     */
    var free: Short = 0

    /**
     * deleted record occupy space
     */
    var garbage: Short = 0

    /**
     * last insert record offset
     */
    var lastInsertOffset: Short = 0

    /**
     * insert direction that use for support insert.
     * 0 is left.
     * 1 is right.
     */
    var direction: Short = 0

    /**
     * number of inserts in the same direction
     */
    var directionCount: Short = 0

    /**
     * the max transaction id in page
     */
    @Unused
    var maxTransactionId: Long = 0

    /**
     * this page in b-tree layer level
     * leaf node level is 0.
     */
    var level: Short = 0

    /**
     * which index the page belong to
     */
    var indexId: Long = 0

    /**
     * join seg leaf 10 bytes
     */
    @Unused
    var segLeafPre: Short = 0

    /**
     * 10 bytes.
     * b-tree leaf-node header info . only root page have.
     */
    @Unused
    var segLeaf: Long = 0

    /**
     * join seg top 10 bytes
     */
    @Unused
    var segTopPre: Short = 0

    /**
     * 10 bytes.
     * b-tree non-leaf-node header info . only root page have.
     */
    var segTop: Long = 0
    override fun length(): Int {
        return ConstantSize.PAGE_HEADER.size()
    }

    override fun toBytes(): ByteArray {
        val buffer: DynamicByteBuffer = DynamicByteBuffer.allocate()
        buffer.appendShort(slotCount)
        buffer.appendShort(heapTop)
        buffer.appendShort(absoluteRecordCount)
        buffer.appendShort(recordCount)
        buffer.appendShort(free)
        buffer.appendShort(garbage)
        buffer.appendShort(lastInsertOffset)
        buffer.appendShort(direction)
        buffer.appendShort(directionCount)
        buffer.appendLong(maxTransactionId)
        buffer.appendShort(level)
        buffer.appendLong(indexId)
        buffer.appendShort(segLeafPre)
        buffer.appendLong(segLeaf)
        buffer.appendShort(segTopPre)
        buffer.appendLong(segTop)
        return buffer.toBytes()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PageHeader

        return (slotCount == other.slotCount &&
                heapTop == other.heapTop &&
                absoluteRecordCount == other.absoluteRecordCount &&
                recordCount == other.recordCount &&
                free == other.free &&
                garbage == other.garbage &&
                lastInsertOffset == other.lastInsertOffset &&
                direction == other.direction &&
                directionCount == other.directionCount &&
                maxTransactionId == other.maxTransactionId &&
                level == other.level &&
                indexId == other.indexId &&
                segLeafPre == other.segLeafPre &&
                segLeaf == other.segLeaf &&
                segTopPre == other.segTopPre &&
                segTop == other.segTop)
    }

    override fun hashCode(): Int {
        var result = slotCount.toInt()
        result = 31 * result + heapTop
        result = 31 * result + absoluteRecordCount
        result = 31 * result + recordCount
        result = 31 * result + free
        result = 31 * result + garbage
        result = 31 * result + lastInsertOffset
        result = 31 * result + direction
        result = 31 * result + directionCount
        result = 31 * result + maxTransactionId.hashCode()
        result = 31 * result + level
        result = 31 * result + indexId.hashCode()
        result = 31 * result + segLeafPre
        result = 31 * result + segLeaf.hashCode()
        result = 31 * result + segTopPre
        result = 31 * result + segTop.hashCode()
        return result
    }


    companion object PageHeaderFactory {

        val EMPTY_PAGE_HEAP_TOP = (ConstantSize.FILE_HEADER.size() +
                ConstantSize.PAGE_HEADER.size() +
                ConstantSize.INFIMUM.size() +
                ConstantSize.SUPREMUM.size()).toShort()

        /**
         * create a empty page header
         */
        fun create() = PageHeader().apply {
            this.slotCount = 2.toShort()
            this.heapTop = EMPTY_PAGE_HEAP_TOP
            this.absoluteRecordCount = 2.toShort()
            this.recordCount = 0.toShort()
            this.free = 0.toShort()
            this.garbage = 0.toShort()
            this.lastInsertOffset = EMPTY_PAGE_HEAP_TOP
            this.level = 0.toShort()
            this.direction = 0.toShort()
            this.directionCount = 0.toShort()
            this.maxTransactionId = 0L
            this.indexId = 0
            this.segLeafPre = 0.toShort()
            this.segLeaf = 0L
            this.segTopPre = 0.toShort()
            this.segTop = 0L
        }

        fun wrap(pageHeaderArr: ByteArray) = PageHeader().apply {
            ConstantSize.PAGE_HEADER.checkSize(pageHeaderArr)
            val buffer = ByteBuffer.wrap(pageHeaderArr)
            this.slotCount = buffer.getShort()
            this.heapTop = buffer.getShort()
            this.absoluteRecordCount = buffer.getShort()
            this.recordCount = buffer.getShort()
            this.free = buffer.getShort()
            this.garbage = buffer.getShort()
            this.lastInsertOffset = buffer.getShort()
            this.direction = buffer.getShort()
            this.directionCount = buffer.getShort()
            this.maxTransactionId = buffer.getLong()
            this.level = buffer.getShort()
            this.indexId = buffer.getLong()
            this.segLeafPre = buffer.getShort()
            this.segLeaf = buffer.getLong()
            this.segTopPre = buffer.getShort()
            this.segTop = buffer.getLong()
        }
    }
}
