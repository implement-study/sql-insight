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

/**
 * 56 bytes.
 * record page info that may change frequently
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class PageHeader : PageObject, ByteWrapper {
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
    var segLeafPre: Short = 0

    /**
     * 10 bytes.
     * b-tree leaf-node header info . only root page have.
     */
    var segLeaf: Long = 0

    /**
     * join seg top 10 bytes
     */
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
}
