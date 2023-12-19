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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;

import lombok.Data;
import org.gongxuanzhang.easybyte.core.ByteWrapper;
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer;

/**
 * describe page total info.
 * in general. file header fixed.
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Data
public class FileHeader implements ByteWrapper, PageObject {


    /**
     * use it with {@link FileTrailer#checkSum}
     **/
    int checkSum;
    /**
     * page offset
     **/
    int offset;
    /**
     * page type
     * {@link PageType}
     **/
    short pageType;
    /**
     * pre page offset
     **/
    int pre;
    /**
     * next page offset
     **/
    int next;
    /**
     * Log Sequence Number 8字节
     * {@link FileTrailer#lsn}
     **/
    long lsn;
    /**
     * system table space
     **/
    long flushLsn;
    /**
     * table space id
     **/
    int spaceId;


    @Override
    public int length() {
        return ConstantSize.FILE_HEADER.size();
    }

    @Override
    public byte[] toBytes() {
        DynamicByteBuffer buffer = DynamicByteBuffer.allocate();
        buffer.appendInt(this.checkSum);
        buffer.appendInt(this.offset);
        buffer.appendShort(this.pageType);
        buffer.appendInt(this.pre);
        buffer.appendInt(this.next);
        buffer.appendLong(this.lsn);
        buffer.appendLong(this.flushLsn);
        buffer.appendInt(this.spaceId);
        return buffer.toBytes();
    }
}
