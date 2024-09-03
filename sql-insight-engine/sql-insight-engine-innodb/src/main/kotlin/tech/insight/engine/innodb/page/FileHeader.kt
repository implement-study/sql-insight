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

import io.netty.buffer.ByteBuf
import org.gongxuanzhang.easybyte.core.ByteWrapper
import tech.insight.engine.innodb.page.type.DataPage.Companion.FIL_PAGE_INDEX_VALUE


/**
 * describe page total info.
 * in general. file header fixed.
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class FileHeader(override val belongPage: InnoDbPage) : ByteWrapper, PageObject {

    val source: ByteBuf = belongPage.source.slice(ConstantSize.FILE_HEADER.offset(), ConstantSize.FILE_HEADER.size())

    /**
     * use it with [FileTrailer.checkSum]
     */
    var checkSum: Int = source.readInt()

    /**
     * page offset
     * 4 bytes
     */
    var offset: Int = source.readInt()

    /**
     * page type
     * 2 bytes
     */
    var pageType: Int = source.readShort().toInt()

    /**
     * pre page offset
     */
    var pre: Int = source.readInt()

    /**
     * next page offset
     */
    var next: Int = source.readInt()

    /**
     * Log Sequence Number 8字节
     * [FileTrailer.lsn]
     */
    var lsn: Long = source.readLong()

    /**
     * system table space
     */
    var flushLsn: Long = source.readLong()

    /**
     * table space id
     */
    var spaceId = source.readInt()

    override fun length(): Int {
        return ConstantSize.FILE_HEADER.size()
    }

    override fun toBytes(): ByteArray {
        return source.array()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FileHeader) return false
        return this.source == other.source
    }

    override fun hashCode(): Int {
        return source.hashCode()
    }

    companion object FileHeaderFactory {

        const val checkSum = 0X12345678

        fun wrap(arr: ByteArray, belongPage: InnoDbPage) = FileHeader(belongPage)

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
