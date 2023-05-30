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


import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.tool.BitUtils;

/**
 * RecordHeader 工厂 {@link RecordHeader}
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class RecordHeaderFactory implements ByteBeanSwapper<RecordHeader> {

    /**
     * heap_no 13位序号  最小值是0 最大值是1
     * record_type  3位类型  最小记录是2 最大记录是3
     **/
    private static final byte MIN_RECORD_HEAP_NO = 0b00000_010;
    private static final byte MAX_RECORD_HEAP_NO = 0b00001_011;

    @Override
    public RecordHeader swap(byte[] bytes) {
        ConstantSize.RECORD_HEADER.checkSize(bytes);
        return new RecordHeader(bytes);
    }



    /**
     * 创建下确界记录头
     **/
    public RecordHeader createInfimumHeader() {
        //  unuseful(1) │ unuseful(1)│delete_mask(1)│min_rec_mask(1)│n_owned(4)│heap_no(13)│record_type(3)
        //  │next_record(16)│
        byte[] supremumOffset = BitUtils.cutToByteArray(ConstantSize.SUPREMUM.offset(), 2);
        return new RecordHeader(new byte[]{
                (byte) 0x01,
                (byte) 0x00,
                MIN_RECORD_HEAP_NO,
                supremumOffset[0],
                supremumOffset[1],
        });
    }

    /**
     * 创建上确界记录头
     **/
    public RecordHeader createSupremumHeader() {
        return new RecordHeader(new byte[]{
                (byte) 0x01,
                (byte) 0x00,
                MAX_RECORD_HEAP_NO,
                (byte) 0b0000_0000,
                (byte) 0b0000_0000,
        });
    }


}
