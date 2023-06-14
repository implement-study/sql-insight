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

import java.nio.ByteBuffer;
import java.util.List;


/**
 * 组合主键
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class CompositePrimaryKey implements PrimaryKey {

    private final List<PrimaryKey> list;

    private int length;

    private boolean dynamic;

    public CompositePrimaryKey(List<PrimaryKey> list) {
        this.list = list;
        for (PrimaryKey primaryKey : list) {
            this.length += primaryKey.length();
            if (primaryKey.isDynamic()) {
                dynamic = true;
            }
        }
    }

    @Override
    public int compareTo(@NotNull PrimaryKey other) {
        if (!(other instanceof CompositePrimaryKey)) {
            throw new IllegalArgumentException("主键异常");
        }
        for (int i = 0; i < this.list.size(); i++) {
            PrimaryKey thisPrimaryKey = this.list.get(i);
            PrimaryKey otherPrimaryKey = ((CompositePrimaryKey) other).list.get(i);
            int compare = thisPrimaryKey.compareTo(otherPrimaryKey);
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
    }

    @Override
    public int length() {
        return this.length;
    }

    @Override
    public boolean isDynamic() {
        return this.dynamic;
    }


    @Override
    public String toString() {
        return this.list.toString();
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(this.length);
        for (PrimaryKey primaryKey : this.list) {
            buffer.put(primaryKey.toBytes());
        }
        return buffer.array();
    }
}
