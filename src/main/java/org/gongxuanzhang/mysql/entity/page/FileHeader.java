/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.entity.page;

import lombok.Data;
import org.gongxuanzhang.mysql.core.ByteSwappable;
import org.gongxuanzhang.mysql.core.factory.ConstantSize;

import java.nio.ByteBuffer;

/**
 * 文件头，描述各种页的通用信息
 * 共38字节
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Data
public class FileHeader implements ByteSwappable<FileHeader> {


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
     * 校验和,和文件尾的校验和一起使用  4字节
     * {@link FileTrailer#checkSum}
     **/
    int checkSum;
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
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(ConstantSize.FILE_HEADER_SIZE.getSize());
        buffer.putInt(this.offset);
        buffer.putShort(this.pageType);
        buffer.putInt(this.pre);
        buffer.putInt(this.next);
        buffer.putInt(checkSum);
        buffer.putLong(lsn);
        buffer.putLong(flushLsn);
        buffer.putInt(spaceId);
        return buffer.array();
    }

    @Override
    public FileHeader fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        this.offset = buffer.getInt();
        this.pageType = buffer.getShort();
        this.pre = buffer.getInt();
        this.next = buffer.getInt();
        this.checkSum = buffer.getInt();
        this.lsn = buffer.getLong();
        this.flushLsn = buffer.getLong();
        this.spaceId = buffer.getInt();
        return this;
    }
}
