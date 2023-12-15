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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLColumnPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLColumnUniqueKey;
import com.alibaba.druid.sql.ast.statement.SQLNotNullConstraint;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import lombok.Data;
import org.gongxuanzhang.sql.insight.core.analysis.druid.CommentVisitor;
import org.gongxuanzhang.sql.insight.core.object.value.Value;
import org.gongxuanzhang.sql.insight.core.object.value.ValueNegotiator;
import org.gongxuanzhang.sql.insight.core.object.value.ValueVisitor;


/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Data
public class Column implements FillDataVisitor, CommentContainer {

    private String name;

    private DataType dataType;

    private boolean autoIncrement;

    private boolean notNull;

    private boolean primaryKey;

    private boolean unique;

    private Value defaultValue;

    private String comment;

    private boolean variable;


    @Override
    public Column setComment(String comment) {
        this.comment = comment;
        return this;
    }


    @Override
    public void endVisit(SQLColumnDefinition x) {
        this.name = x.getColumnName();
        this.autoIncrement = x.isAutoIncrement();
        this.dataType = new DataType();
        x.accept(dataType);
        if (dataType.getType() == DataType.Type.VARCHAR) {
            this.variable = true;
        }
        x.accept(new ConstraintVisitor());
        if (x.getComment() != null) {
            x.getComment().accept(new CommentVisitor(this));
        }
        SQLExpr defaultExpr = x.getDefaultExpr();
        if (defaultExpr != null) {
            defaultExpr.accept(new ValueVisitor(ValueNegotiator.columnDefaultValue(this)));
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


}
