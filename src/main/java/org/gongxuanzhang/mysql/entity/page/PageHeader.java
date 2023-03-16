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
import org.gongxuanzhang.mysql.annotation.Unused;
import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.core.ByteSwappable;
import org.gongxuanzhang.mysql.entity.ShowLength;

import java.nio.ByteBuffer;


/**
 * 页头，56字节
 * 记录页的状态信息
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Data
public class PageHeader implements ShowLength, ByteSwappable {

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
    @Unused
    short direction;
    /**
     * 2字节/一个方向连续插入的记录数量
     **/
    @Unused
    short directionCount;

    /**
     * 8字节/修改当前页的最大事务ID，该值仅在二级索引中定义
     **/
    @Unused
    long maxTransactionId;

    /**
     * 2字节 当前页在B+树中所处的层级
     **/
    short level;

    /**
     * 8字节 索引ID,表示当前页属于哪个索引
     **/
    @Unused
    long indexId;
    /**
     * 10字节 b+树叶子段的头部信息 只有root才有意义
     **/
    @Unused
    long segLeaf;
    /**
     * 10字节 b+树 非叶子结点段的头部信息 只有root才有意义
     **/
    @Unused
    long segTop;


    @Override
    public int length() {
        return ConstantSize.PAGE_HEADER_SIZE.getSize();
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(ConstantSize.PAGE_HEADER_SIZE.getSize());
        buffer.putShort(slotCount);
        buffer.putShort(heapTop);
        buffer.putShort(absoluteRecordCount);
        buffer.putShort(recordCount);
        buffer.putShort(free);
        buffer.putShort(garbage);
        buffer.putShort(lastInsert);
        buffer.putShort(direction);
        buffer.putShort(directionCount);
        buffer.putLong(maxTransactionId);
        buffer.putShort(level);
        buffer.putLong(indexId);
        buffer.putLong(segLeaf);
        buffer.putLong(segTop);
        return buffer.array();
    }
}
