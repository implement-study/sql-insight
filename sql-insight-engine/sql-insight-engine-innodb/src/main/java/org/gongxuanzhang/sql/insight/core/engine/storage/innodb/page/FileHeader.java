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
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.core.ShowLength;

/**
 * describe page total info.
 * in general. file header fixed.
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Data
public class FileHeader implements ByteWrapper, ShowLength {


    /**
     * 校验和,和文件尾的校验和一起使用  4字节
     * {@link FileTrailer#checkSum}
     **/
    int checkSum;
    /**
     * 偏移量,页号 4字节int
     **/
    int offset;
    /**
     * 页类型 2字节表示
     **/
    short pageType;
    /**
     * 上一页 4字节
     **/
    int pre;
    /**
     * 下一页 4字节
     **/
    int next;
    /**
     * Log Sequence Number 8字节
     * 和文件尾一起校验使用
     * {@link FileTrailer#lsn}
     **/
    long lsn;
    /**
     * 系统表空间的定义
     **/
    long flushLsn;
    /**
     * 所属表空间
     **/
    int spaceId;


    @Override
    public int length() {
        return ConstantSize.FILE_HEADER.getSize();
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
