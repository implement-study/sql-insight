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

package org.gongxuanzhang.sql.insight.core.object;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLColumnPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLColumnUniqueKey;
import com.alibaba.druid.sql.ast.statement.SQLNotNullConstraint;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class Column implements FillDataVisitor {

    private String name;

    private DataTypeEnum dataTypeEnum;

    private boolean autoIncrement;

    private boolean notNull;

    private boolean primaryKey;

    private boolean unique;

    private String defaultValue;

    private String comment;

    @Override
    public void endVisit(SQLColumnDefinition x) {
        this.name = x.getColumnName();
        this.autoIncrement = x.isAutoIncrement();
        x.accept(new DataTypeVisitor());
        x.accept(new ConstraintVisitor());
        if (x.getComment() != null) {
            this.comment = x.getComment().toString();
        }
        SQLExpr defaultExpr = x.getDefaultExpr();
        if (defaultExpr != null) {
            defaultExpr.accept(new ValueVisitor());
        }

    }


    /**
     * create a data type via visitor
     **/
    private class DataTypeVisitor implements SQLASTVisitor {


        @Override
        public void endVisit(SQLDataType x) {
            dataTypeEnum = DataTypeEnum.valueOf(x.getName().toUpperCase());
        }


        @Override
        public void endVisit(SQLCharacterDataType x) {
            int length = x.getLength();
        }
    }

    private class ConstraintVisitor implements SQLASTVisitor {
        @Override
        public void endVisit(SQLColumnUniqueKey x) {
            Column.this.unique = true;
        }

        @Override
        public void endVisit(SQLNotNullConstraint x) {
            Column.this.notNull = true;
        }

        @Override
        public void endVisit(SQLColumnPrimaryKey x) {
            Column.this.primaryKey = true;
        }
    }


    private class ValueVisitor implements SQLASTVisitor {

        @Override
        public void endVisit(SQLIntegerExpr x) {
            Column.this.defaultValue = x.toString();
        }
    }

    public String getName() {
        return name;
    }

    public DataTypeEnum getDataTypeEnum() {
        return dataTypeEnum;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getComment() {
        return comment;
    }

    public boolean isUnique() {
        return unique;
    }
}
