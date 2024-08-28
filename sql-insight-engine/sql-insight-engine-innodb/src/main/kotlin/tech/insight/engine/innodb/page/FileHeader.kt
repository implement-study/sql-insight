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

import java.nio.ByteBuffer
import org.gongxuanzhang.easybyte.core.ByteWrapper
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.engine.innodb.page.type.DataPage.Companion.FIL_PAGE_INDEX_VALUE


/**
 * describe page total info.
 * in general. file header fixed.
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class FileHeader private constructor(override val belongPage: InnoDbPage) : ByteWrapper, PageObject {


    /**
     * use it with [FileTrailer.checkSum]
     */
    var checkSum: Int = 0

    /**
     * page offset
     */
    var offset = 0

    /**
     * page type
     */
    var pageType: Short = 0

    /**
     * pre page offset
     */
    var pre = 0

    /**
     * next page offset
     */
    var next = 0

    /**
     * Log Sequence Number 8字节
     * [FileTrailer.lsn]
     */
    var lsn: Long = 0

    /**
     * system table space
     */
    var flushLsn: Long = 0

    /**
     * table space id
     */
    var spaceId = 0


    override fun length(): Int {
        return ConstantSize.FILE_HEADER.size()
    }

    override fun toBytes(): ByteArray {
        val buffer: DynamicByteBuffer = DynamicByteBuffer.allocate()
        buffer.appendInt(checkSum)
        buffer.appendInt(offset)
        buffer.appendShort(pageType)
        buffer.appendInt(pre)
        buffer.appendInt(next)
        buffer.appendLong(lsn)
        buffer.appendLong(flushLsn)
        buffer.appendInt(spaceId)
        return buffer.toBytes()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FileHeader) return false

        return (checkSum == other.checkSum &&
                offset == other.offset &&
                pageType == other.pageType &&
                pre == other.pre &&
                next == other.next &&
                lsn == other.lsn &&
                flushLsn == other.flushLsn &&
                spaceId == other.spaceId)
    }

    override fun hashCode(): Int {
        var result = checkSum
        result = 31 * result + offset
        result = 31 * result + pageType
        result = 31 * result + pre
        result = 31 * result + next
        result = 31 * result + lsn.hashCode()
        result = 31 * result + flushLsn.hashCode()
        result = 31 * result + spaceId
        return result
    }


    companion object FileHeaderFactory {

        fun wrap(arr: ByteArray, belongPage: InnoDbPage) = FileHeader(belongPage).apply {
            ConstantSize.FILE_HEADER.checkSize(arr)
            val buffer = ByteBuffer.wrap(arr)
            this.checkSum = buffer.getInt()
            this.offset = buffer.getInt()
            this.pageType = buffer.getShort()
            this.pre = buffer.getInt()
            this.next = buffer.getInt()
            this.lsn = buffer.getLong()
            this.flushLsn = buffer.getLong()
            this.spaceId = buffer.getInt()
        }

        /**
         * create a empty file header
         */
        fun create(belongPage: InnoDbPage) = FileHeader(belongPage).apply {
            this.next = 0
            this.pre = 0
            this.offset = 0
            this.pageType = FIL_PAGE_INDEX_VALUE
        }
    }
}
