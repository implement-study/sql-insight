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


/**
 * 列类型
 * 有一个默认长度，如果默认长度是-1 表示用户必须输入
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public enum ColumnType {

    /**
     * 数字
     **/
    INT(4),
    /**
     * 字符串
     **/
    VARCHAR(255, true),
    /**
     * 时间戳
     **/
    TIMESTAMP(-1),
    /**
     * null
     **/
    NULL(-1);


    private final int length;

    /**
     * 是否是变长字段
     **/
    private final boolean dynamic;

    ColumnType(int length, boolean dynamic) {
        this.length = length;
        this.dynamic = dynamic;
    }

    ColumnType(int length) {
        this.length = length;
        this.dynamic = false;
    }


    public int getLength() {
        return length;
    }

    public boolean isDynamic() {
        return this.dynamic;
    }
}
