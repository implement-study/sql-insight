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
import org.gongxuanzhang.sql.insight.core.auth.User;
import org.gongxuanzhang.sql.insight.core.object.Database;
import org.gongxuanzhang.sql.insight.core.object.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class SessionContext extends AbstractMapContext {

    private static final ThreadLocal<SessionContext> HOLDER = ThreadLocal.withInitial(SessionContext::new);


    @Temporary(detail = "temp for thread")
    public static SessionContext getCurrentSession() {
        return HOLDER.get();
    }

    @Temporary
    public static void clearSession() {
        HOLDER.remove();
    }

    private Database database;

    private final Map<String, String> tableAlias = new HashMap<>();

    public void tableAlias(String name, String alias) {
        this.tableAlias.put(name, alias);
    }

    public Table getTableByNameOrAlias(String nameOrAlias) {
        String convertName = this.tableAlias.get(nameOrAlias);
        if (convertName == null) {
            convertName = nameOrAlias;
        }
        TableDefinitionManager tableDefinitionManager = SqlInsightContext.getInstance().getTableDefinitionManager();
        return tableDefinitionManager.select(this.database.getName(), convertName);
    }

    public User getCurrentUser() {
        return null;
    }

    public Database currentDatabase() {
        return this.database;
    }

    public void useDatabase(Database database) {
        this.database = database;
    }

}
