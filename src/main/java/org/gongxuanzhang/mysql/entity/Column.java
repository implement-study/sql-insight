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

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnConstraint;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLColumnUniqueKey;
import com.alibaba.druid.sql.ast.statement.SQLNotNullConstraint;
import lombok.Data;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.tool.SqlAssert;
import org.gongxuanzhang.mysql.tool.SqlUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 列信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class Column implements ExecuteInfo {

    public static final int MAX_SIZE = 0xff;

    private ColumnType type;
    private String name;
    private String comment;
    private boolean autoIncrement;
    private boolean notNull;
    private boolean unique;
    private DefaultValue<?> defaultValue;
    private boolean dynamic;
    /**
     * 如果此字段可以为null 作为整个表null值列表中的第几个
     **/
    private Integer nullIndex;
    private Integer length;


    public Column() {

    }

    public Column(SQLColumnDefinition definition) throws SqlParseException {
        this.autoIncrement = definition.isAutoIncrement();
        this.name = SqlUtils.trimSqlEsc(definition.getColumnName());
        analysisType(definition.getDataType());
        analysisConstraint(definition.getConstraints());
        analysisDefault(definition.getDefaultExpr());
        if (definition.getComment() != null) {
            this.comment = SqlUtils.trimSqlEsc(definition.getComment().toString());
        }
    }

    /**
     * 约束校验 如果错误会抛异常
     * 如果有类型转换会转换
     **/
    public void check(Cell<?> cell) throws MySQLException {
        if (this.notNull && cell.getValue() == null) {
            throw new MySQLException(String.format("%s列不允许为null", this.getName()));
        }
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
            } else if (constraint instanceof SQLColumnUniqueKey) {
                this.unique = true;
            }
        }
    }

    private void analysisType(SQLDataType dataType) throws SqlParseException {
        this.type = ColumnType.valueOf(dataType.getName().toUpperCase());
        this.dynamic = type.isDynamic();
        if (CollectionUtils.isEmpty(dataType.getArguments())) {
            this.length = type.getLength();
            SqlAssert.isTure(length != 1, this.name + " type: " + dataType.getName() + "没有长度");
            return;
        }
        SQLExpr sqlExpr = dataType.getArguments().get(0);
        this.length = (Integer) ((SQLIntegerExpr) sqlExpr).getValue();
        SqlAssert.between(1, MAX_SIZE, this.length);

    }

}
