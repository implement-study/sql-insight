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

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnConstraint;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLNotNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLUniqueConstraint;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 列信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class ColumnInfo implements ExecuteInfo {

    private ColumnType type;
    private String name;
    private String comment;
    private boolean autoIncrement;
    private boolean notNull;
    private boolean unique;
    private DefaultValue<?> defaultValue;
    private Integer length;


    public ColumnInfo() {

    }

    public ColumnInfo(SQLColumnDefinition definition) {
        this.autoIncrement = definition.isAutoIncrement();
        analysisType(definition.getDataType());
        analysisConstraint(definition.getConstraints());
        this.name = definition.getColumnName();
        analysisDefault(definition.getDefaultExpr());
    }

    private void analysisDefault(SQLExpr defaultExpr) {
        if (defaultExpr == null) {
            return;
        }
        if (defaultExpr instanceof SQLCharExpr) {
            String defaultValue = ((SQLCharExpr) defaultExpr).getValue().toString();
            this.defaultValue = new StringDefaultValue(defaultValue);
        } else if (defaultExpr instanceof SQLIntegerExpr) {
            Integer value = (Integer) ((SQLIntegerExpr) defaultExpr).getValue();
            this.defaultValue = new IntegerDefaultValue(value);
        }

    }

    private void analysisConstraint(List<SQLColumnConstraint> constraints) {
        if (CollectionUtils.isEmpty(constraints)) {
            return;
        }
        for (SQLColumnConstraint constraint : constraints) {
            if (constraint instanceof SQLNotNullConstraint) {
                this.notNull = true;
            } else if (constraint instanceof SQLUniqueConstraint) {
                this.unique = true;
            }
        }
    }

    private void analysisType(SQLDataType dataType) {
        this.type = ColumnType.valueOf(dataType.getName().toUpperCase());
        if (!CollectionUtils.isEmpty(dataType.getArguments())) {
            SQLExpr sqlExpr = dataType.getArguments().get(0);
            if (sqlExpr instanceof SQLIntegerExpr) {
                this.length = (Integer) ((SQLIntegerExpr) sqlExpr).getValue();
            }
        }
    }

}
