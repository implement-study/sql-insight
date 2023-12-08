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

import org.gongxuanzhang.sql.insight.core.annotation.Temporary;
import org.gongxuanzhang.sql.insight.core.object.Row;
import org.gongxuanzhang.sql.insight.core.result.ResultInterface;
import org.gongxuanzhang.sql.insight.core.result.SelectResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * context during execute
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class ExecuteContext extends AbstractMapContext {

    private final String sql;

    private final List<Row> rows = new ArrayList<>();

    public ExecuteContext(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }


    @Temporary
    public ResultInterface toResult() {
        return new SelectResult(rows);
    }

    public void addRow(Row row) {
        this.rows.add(row);
    }

    public TableDefinitionManager getTableDefinitionManager() {
        return getSqlInsightContext().getTableDefinitionManager();
    }

    public SqlInsightContext getSqlInsightContext() {
        return SqlInsightContext.getInstance();
    }

    public GlobalContext getGlobalContext() {
        return GlobalContext.getInstance();
    }

    public SessionContext getSessionContext() {
        return SessionContext.getCurrentSession();
    }

    @Nullable
    @Override
    public String get(String key) {
        String s = getSessionContext().get(key);
        if (s != null) {
            return s;
        }
        return getGlobalContext().get(key);
    }


    @Override
    public void put(String key, String value) {
        throw new UnsupportedOperationException("execute context can't support put method");
    }


    @Nullable
    @Override
    public String remove(String key) {
        throw new UnsupportedOperationException("execute context can't support remove method");
    }
}
