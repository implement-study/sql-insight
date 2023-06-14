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

package org.gongxuanzhang.mysql.entity.page;

import org.gongxuanzhang.mysql.core.ByteSwappable;
import org.gongxuanzhang.mysql.entity.ShowLength;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * compact 的null值列表
 * 固定占两字节，本质上是一个short数字位图
 *
 * @author gongxuanzhang
 **/
public class CompactNullValue implements ByteSwappable, ShowLength {


    short value;

    public CompactNullValue(short value) {
        this.value = value;
    }

    public CompactNullValue() {
        this((short) 0);
    }

    public boolean isNull(int index) {
        int mask = 1 << index;
        return (mask & this.value) == mask;
    }


    @Override
    public int length() {
        return 2;
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[2];
        result[1] = (byte) value;
        result[0] = (byte) (value >> 8);
        return result;
    }


    public void setNull(int index) throws MySQLException {
        if (index > 15) {
            throw new MySQLException("目前最多支持16个null列");
        }
        this.value |= (1 << index);
    }


    /**
     * 返回所有null值的列的角标
     * 返回的列是反过来的
     *
     * @return [0, 1] 表示0列和1列为null
     **/
    public List<Integer> nullColIndex() {
        List<Integer> nullIndex = new ArrayList<>();
        short base = this.value;
        int index = 0;
        while (base != 0) {
            if ((base & 1) == 1) {
                nullIndex.add(index);
            }
            base >>= 1;
            index++;
        }
        return nullIndex;

    }
}
