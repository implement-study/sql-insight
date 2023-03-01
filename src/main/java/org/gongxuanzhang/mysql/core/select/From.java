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

package org.gongxuanzhang.mysql.core.select;

import lombok.Data;
import org.gongxuanzhang.mysql.core.TableInfoBox;
import org.gongxuanzhang.mysql.entity.TableInfo;

/**
 * select 中的from内容
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class From implements TableInfoBox {


    /**
     * 查询主表
     **/
    private TableInfo main;

    @Override
    public TableInfo getTableInfo() {
        return this.main;
    }

    @Override
    public void setTableInfo(TableInfo tableInfo) {
        this.main = tableInfo;
    }


}
