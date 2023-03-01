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

package org.gongxuanzhang.mysql.entity;

import lombok.Data;
import org.gongxuanzhang.mysql.annotation.DependOnContext;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gongxuanzhang
 */
@Data
@DependOnContext
public class IncrementKey implements Serializable {
    private final String colName;
    private AtomicInteger incrementValue = new AtomicInteger(0);

    public IncrementKey(String colName) {
        this.colName = colName;
    }

    /**
     * 拿到自增主键的值
     *
     * @return 拿到下一个应该获取的主键值
     */

    public int nextKey() {
        return incrementValue.incrementAndGet();
    }

    public void check(int value) {
        if (incrementValue.get() < value) {
            incrementValue.set(value);
        }
    }

}
