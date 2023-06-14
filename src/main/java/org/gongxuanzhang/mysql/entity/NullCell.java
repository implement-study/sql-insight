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

/**
 * null cell
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public final class NullCell implements Cell<Object> {

    @Override
    public ColumnType getType() {
        return ColumnType.NULL;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public PrimaryKey toPrimaryKey() throws MySQLException {
        throw new MySQLException("主键不能为空");
    }

    @Override
    public String toString() {
        return "(null)";
    }


}
