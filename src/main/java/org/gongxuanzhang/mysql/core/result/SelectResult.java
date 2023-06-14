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

package org.gongxuanzhang.mysql.core.result;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 查询结果返回实体
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class SelectResult implements Result {

    private final List<String> head;

    private final List<Map<String, String>> data;

    private String sqlTime;

    private String sql;

    @Override
    public int getCode() {
        return 200;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public String getSqlTime() {
        return sqlTime;
    }

    @Override
    public void setSqlTime(String sqlTime) {
        this.sqlTime = sqlTime;
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public void setSql(String sql) {
        this.sql = sql;
    }
}
