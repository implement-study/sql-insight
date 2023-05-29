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


import org.gongxuanzhang.mysql.exception.MySQLException;

/**
 * 一个单元格的数据
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface Cell<T> extends ExecuteInfo, ShowLength {

    /**
     * 数据类型
     *
     * @return 不能为空
     **/
    ColumnType getType();

    /**
     * 返回具体值
     *
     * @return 可以为空
     **/
    T getValue();

    /**
     * 单元格转换成字节数组
     *
     * @return 字节数组 如果是null返回byte[0]
     **/
    byte[] toBytes();


    /**
     * 一个cell的长度
     *
     * @return 字节数组的长度。如果是动态长度返回-1
     **/
    @Override
    int length();


    /**
     * 单元格转换成主键
     *
     * @return 转成主键
     **/
    PrimaryKey toPrimaryKey() throws MySQLException;
}



