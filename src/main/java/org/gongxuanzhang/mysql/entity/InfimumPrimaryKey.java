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

import org.jetbrains.annotations.NotNull;


/**
 * 下确界主键
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InfimumPrimaryKey implements PrimaryKey {


    @Override
    public int compareTo(@NotNull PrimaryKey other) {
        if (other instanceof InfimumPrimaryKey) {
            return 0;
        }
        return -1;
    }

    @Override
    public int length() {
        return -1;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }


    @Override
    public byte[] toBytes() {
        throw new UnsupportedOperationException("下确界主键不支持to bytes");
    }
}
