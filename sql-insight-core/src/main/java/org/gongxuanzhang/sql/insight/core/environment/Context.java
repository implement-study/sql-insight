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

package org.gongxuanzhang.sql.insight.core.environment;

import org.jetbrains.annotations.Nullable;

/**
 * The top-level interface of the context
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public interface Context {

    /**
     * put a pair in context
     *
     * @param key   key unique
     * @param value value
     **/
    void put(String key, String value);

    /**
     * get value from context
     *
     * @param key pair of key
     * @return maybe null
     **/
    @Nullable
    String get(String key);

    /**
     * remove pair from context
     *
     * @param key
     * @return return the removed value if exists , else return null
     **/
    @Nullable
    String remove(String key);


}
