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

package org.gongxuanzhang.mysql.entity;

import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.tool.BitUtils;

/**
 * int单元格
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class IntCell implements Cell<Integer> {


    private final Integer value;

    public IntCell(Integer value) {
        this.value = value;
    }

    public IntCell(Cell<?> cell) throws MySQLException {
        try {
            this.value = Integer.parseInt(cell.getValue().toString());
        } catch (NumberFormatException e) {
            throw new MySQLException(cell.getValue() + "不能转换成int");
        }

    }

    @Override
    public ColumnType getType() {
        return ColumnType.INT;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public byte[] toBytes() {
        return BitUtils.cutToByteArray(this.value, 4);
    }

    @Override
    public int length() {
        return 4;
    }

    @Override
    public PrimaryKey toPrimaryKey() {
        return new IntegerPrimaryKey(this.value);
    }


    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
