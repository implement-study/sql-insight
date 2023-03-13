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

package org.gongxuanzhang.mysql.core;

/**
 * 可以通过字节数组转换
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface ByteSwappable<T> {


    /**
     * 转换成字节数组
     *
     * @return 转成字节数组
     **/
    byte[] toBytes();

    /**
     * 从字节数组转换过来
     *
     * @param bytes 字节数组
     *
     * @return 转成实体
     */
    T fromBytes(byte[] bytes);
}
