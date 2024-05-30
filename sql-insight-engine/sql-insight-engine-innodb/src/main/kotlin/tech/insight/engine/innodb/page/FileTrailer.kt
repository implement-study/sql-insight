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

/**
 * use it with [FileHeader]
 * only in order to check.
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class FileTrailer private constructor(override val belongPage: InnoDbPage) : ByteWrapper, PageObject {
    /**
     * use it with [FileHeader.checkSum]
     */
    var checkSum = 0

    /**
     * use it with [FileHeader.lsn]
     */
    var lsn = 0

    override fun length(): Int {
        return ConstantSize.FILE_TRAILER.size()
    }

    override fun toBytes(): ByteArray {
        val buffer = ByteBuffer.allocate(length())
        buffer.putInt(checkSum)
        buffer.putInt(lsn)
        return buffer.array()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FileTrailer) return false

        if (checkSum != other.checkSum) return false
        if (lsn != other.lsn) return false

        return true
    }

    override fun hashCode(): Int {
        var result = checkSum
        result = 31 * result + lsn
        return result
    }


    companion object FileTrailerFactory {
        fun create(belongPage: InnoDbPage) = FileTrailer(belongPage)

        fun wrap(bytes: ByteArray, belongPage: InnoDbPage) = run {
            ConstantSize.FILE_TRAILER.checkSize(bytes)
            FileTrailer(belongPage)
        }.apply {
            val buffer: DynamicByteBuffer = DynamicByteBuffer.wrap(bytes)
            this.checkSum = buffer.int
            this.lsn = buffer.int
        }
    }
}
