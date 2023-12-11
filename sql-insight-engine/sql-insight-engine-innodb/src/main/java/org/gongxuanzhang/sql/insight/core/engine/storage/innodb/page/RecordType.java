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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;


/**
 * Compact行格式的记录类型
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public enum RecordType {
    /**
     * leaf-node
     **/
    NORMAL(0x00),
    /**
     * non-leaf-node(index)
     **/
    PAGE(0x01),
    /**
     * infimum
     **/
    INFIMUM(0x02),
    /**
     * supremum
     **/
    SUPREMUM(0x03);


    final int value;

    RecordType(int value) {
        this.value = value;
    }


}
