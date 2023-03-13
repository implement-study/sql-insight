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

package org.gongxuanzhang.mysql.core.convert;

/**
 * 转换器，负责转换字符和目标数据
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface Converter<T> {


    /**
     * 表示的字符串 转换为目标对象
     *
     * @param value 字符串
     *
     * @return 可以为空
     **/
    T convert(String value);


}
