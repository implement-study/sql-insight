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

import java.nio.ByteBuffer;

/**
 * 记录头信息 占5字节
 * 一共40位
 * ┌────────────┬────────────┬──────────────┬───────────────┬──────────┬───────────┬──────────────┬───────────────┐
 * │unuseful(1) │ unuseful(1)│delete_mask(1)│min_rec_mask(1)│n_owned(4)│heap_no(13)│record_type(3)│next_record(16)│
 * └────────────┴────────────┴──────────────┴───────────────┴──────────┴───────────┴──────────────┴───────────────┘
 * delete_mask: 删除标记
 * min_rec_mask: 非叶子节点中的最小记录，只有目录项记录此处可能是1
 * n_owned 当前组中拥有的记录数,只有组中最大的记录才会有此项记录。 页中会分成多组，为了在页中二分查找
 * heap_no 当前记录在本页中的序号，最小是记录是0 最大记录是1 用户记录是从2开始的
 * record_type 0表示普通记录 1表示非叶子结点记录 2表示最小记录 3表示最大记录
 * next_record 下一条记录的偏移量(其实就是本记录的长度)
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public class RecordHeader {

    /**
     * 记录头本质上只有5个字节 但是40bit有不同作用
     **/
    final ByteBuffer source;


    public RecordHeader(byte[] source) {
        this.source = ByteBuffer.wrap(source);
    }


    /**
     * 是否删除
     *
     * @return true删除
     **/
    public boolean isDelete() {
        final int deleteMask = 1 << 5;
        return (source.get(0) & deleteMask) == deleteMask;
    }


    /**
     * 是否是非叶子结点的最小记录
     **/
    public boolean minRec() {
        final int deleteMask = 1 << 4;
        return (source.get(0) & deleteMask) == deleteMask;
    }


    /**
     * 返回当前节点被引用次数
     **/
    public int nOwned() {
        final int nOwned = 0x0F;
        return source.get(0) & nOwned;
    }

    /**
     * 返回节点在当前页中的顺序
     **/
    public int heapNo() {
        byte high = source.get(1);
        byte low = source.get(2);
        return (high << 8 | low) >> 3;
    }


    /**
     * 返回记录类型
     **/
    public RecordType recordType() {
        int type = source.get(2) & 0x07;
        for (RecordType recordType : RecordType.values()) {
            if (recordType.value == type) {
                return recordType;
            }
        }
        throw new IllegalArgumentException("非法");
    }

    /**
     * 返回下一条记录的偏移量
     **/
    public int nextRecordOffset() {
        byte high = source.get(3);
        byte low = source.get(4);
        return (high << 8 | low);
    }


}
