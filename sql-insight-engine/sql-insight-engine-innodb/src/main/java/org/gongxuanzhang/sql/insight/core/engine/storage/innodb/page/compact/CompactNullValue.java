/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact;


import org.gongxuanzhang.easybyte.core.ByteWrapper;
import org.gongxuanzhang.sql.insight.core.exception.SqlInsightException;

import java.util.ArrayList;
import java.util.List;

/**
 * 2 bytes  16 bits
 *
 * @author gongxuanzhang
 **/
public class CompactNullValue implements ByteWrapper {

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


    public void setNull(int index) {
        if (index >= Short.BYTES) {
            throw new SqlInsightException("a table column size must less 16");
        }
        this.value |= (short) (1 << index);
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

    @Override
    public byte[] toBytes() {
        return new byte[]{((byte) (this.value >> Byte.SIZE)), (byte) this.value};
    }
}
