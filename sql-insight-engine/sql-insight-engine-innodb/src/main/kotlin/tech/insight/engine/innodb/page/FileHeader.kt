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
import tech.insight.core.annotation.Unused


/**
 * describe page total info.
 * in general. file header fixed.
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class FileHeader(override val belongPage: InnoDbPage) : PageObject {

    val source: ByteBuf = belongPage.source.slice(ConstantSize.FILE_HEADER.offset, ConstantSize.FILE_HEADER.size)

    /**
     * use it with [FileTrailer.checkSum]
     */
    var checkSum: Int = source.readInt()
        set(value) {
            if (field == value) {
                return
            }
            field = value
            source.setInt(0, value)
        }

    /**
     * page offset
     * 4 bytes
     */
    var offset: Int = source.readInt()
        set(value) {
            if (field == value) {
                return
            }
            field = value
            source.setInt(4, value)
        }

    /**
     * page type
     * 2 bytes
     */
    var pageType: Int = source.readShort().toInt()
        set(value) {
            if (field == value) {
                return
            }
            field = value
            source.setShort(8, value)
        }

    /**
     * pre page offset
     */
    var pre: Int = source.readInt()
        set(value) {
            if (field == value) {
                return
            }
            field = value
            source.setInt(10, value)
        }

    /**
     * next page offset
     */
    var next: Int = source.readInt()
        set(value) {
            if (field == value) {
                return
            }
            field = value
            source.setInt(14, value)
        }

    /**
     * Log Sequence Number 8字节
     * [FileTrailer.lsn]
     */
    var lsn: Long = source.readLong()
        set(value) {
            if (field == value) {
                return
            }
            field = value
            source.setLong(18, value)
        }

    /**
     * system table space
     */
    @Unused
    var flushLsn: Long = source.readLong()
        set(value) {
            if (field == value) {
                return
            }
            field = value
            source.setLong(26, value)
        }

    /**
     * table space id
     */
    @Unused
    var spaceId = source.readInt()
        set(value) {
            if (field == value) {
                return
            }
            field = value
            source.setInt(34, value)
        }

    override fun length(): Int {
        return ConstantSize.FILE_HEADER.size
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

    companion object {

        const val checkSum = 0X12345678
    }
}
