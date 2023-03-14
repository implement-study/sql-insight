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
 * 页头，56字节
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Data
public class PageHeader implements ByteSwappable<PageHeader> {

    /**
     * 页中slot的数量，2字节
     * MySQL中叫 PAGE_N_DIR_SLOTS
     **/
    short slotCount;

    /**
     * 页中还没使用的最小地址，2字节
     **/
    short heapTop;
    /**
     * 页中的记录数量 2字节(包括最小记录和最大记录)
     * MySQL中叫 PAGE_N_HEAP
     **/
    short absoluteRecordCount;
    /**
     * 2字节|该页中记录的数量 (不包括最小和最大记录以及被标记为删除的记录)
     **/
    short recordCount;
    /**
     * 第一个被标记删除的地址，可以通过next_record找到删除列表
     * 2字节
     **/
    short free;
    /**
     * 2字节|已删除记录占用的字节数
     **/
    short garbage;
    /**
     * 2字节/最后插入记录的位置
     **/
    short lastInsert;
    /**
     * 2字节/记录插入的方向
     **/
    short direction;
    /**
     * 2字节/一个方向连续插入的记录数量
     **/
    short directionCount;

    /**
     * 8字节/修改当前页的最大事务ID，该值仅在二级索引中定义
     **/
    long maxTransactionId;

    /**
     * 2字节 当前页在B+树中所处的层级
     **/
    short level;

    /**
     * 8字节 索引ID,表示当前页属于哪个索引
     **/
    long indexId;
    /**
     * 10字节 b+树叶子段的头部信息 只有root才有意义
     **/
    long segLeaf;
    /**
     * 10字节 b+树 非叶子结点段的头部信息 只有root才有意义
     **/
    long segTop;


    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(ConstantSize.PAGE_HEADER_SIZE.getSize());
        buffer.putShort(this.slotCount);
        buffer.putShort(this.heapTop);
        buffer.putShort(this.absoluteRecordCount);
        buffer.putShort(this.recordCount);
        buffer.putShort(this.free);
        buffer.putShort(this.garbage);
        buffer.putShort(this.lastInsert);
        buffer.putShort(this.direction);
        buffer.putShort(this.directionCount);
        buffer.putLong(this.maxTransactionId);
        buffer.putShort(this.level);
        buffer.putLong(this.indexId);
        buffer.putLong(this.segLeaf);
        buffer.putLong(this.segTop);
        return buffer.array();
    }

    @Override
    public PageHeader fromBytes(byte[] bytes) {
        ConstantSize.PAGE_HEADER_SIZE.checkSize(bytes);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        this.slotCount = buffer.getShort();
        this.heapTop = buffer.getShort();
        this.absoluteRecordCount = buffer.getShort();
        this.recordCount = buffer.getShort();
        this.free = buffer.getShort();
        this.garbage = buffer.getShort();
        this.lastInsert = buffer.getShort();
        this.direction = buffer.getShort();
        this.directionCount = buffer.getShort();
        this.maxTransactionId = buffer.getLong();
        this.level = buffer.getShort();
        this.indexId = buffer.getLong();
        this.segLeaf = buffer.getLong();
        this.segTop = buffer.getLong();
        return this;
    }
}
