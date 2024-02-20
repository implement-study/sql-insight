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
 * describe page total info.
 * in general. file header fixed.
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
class FileHeader : ByteWrapper, PageObject {
    /**
     * use it with [FileTrailer.checkSum]
     */
    var checkSum = 0

    /**
     * page offset
     */
    var offset = 0

    /**
     * page type
     * [PageType]
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
}
