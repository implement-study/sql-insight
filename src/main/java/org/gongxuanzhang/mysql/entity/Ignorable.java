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

/**
 * 可忽略的实体
 * 一般来说存在于sql  not if exists
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface Ignorable {


    /**
     * 设置是否忽略 not if exists
     *
     * @param notIfExists 是否忽略
     **/
    default void setNotIfExists(boolean notIfExists) {

    }

    /**
     * 设置 if exists
     *
     * @param ifExists 是否忽略
     **/
    default void setIfExists(boolean ifExists) {

    }

    /**
     * 存在就忽略，默认为true
     *
     * @return 如果是false 目标存在会报错
     **/
    default boolean notIfExists() {
        return false;
    }

    /**
     * 不存在就忽略
     *
     * @return 如果是false 目标不存在会报错
     **/
    default boolean ifExists() {
        return false;
    }


}
