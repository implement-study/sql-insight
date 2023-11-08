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

package org.gongxuanzhang.sql.insight.core.factory;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import org.gongxuanzhang.sql.insight.core.object.Table;

/**
 * use for create table in anyway
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public abstract class TableFactory {

    private TableFactory() {

    }


    /**
     * create a {@code Table} object when create table sql
     * @return 返回个啥
     **/
    public static Table fromCreateTable(MySqlCreateTableStatement statement){
        Table table = new Table();
        statement.accept0(new MySqlASTVisitor() {
            @Override
            public boolean visit(MySqlCreateTableStatement x) {
                System.out.println(x);
                return true;
            }
        });
        SQLName name = statement.getName();
        name.accept(new SQLASTVisitor(){

            @Override
            public void preVisit(SQLObject x) {
                System.out.println(x);
            }

            @Override
            public void postVisit(SQLObject x) {
                System.out.println(x);
            }
        });
        return table;
    }


}
