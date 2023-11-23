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

package org.gongxuanzhang.sql.insight.core.object.value;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.function.Consumer;

/**
 * visitor a value expr ,work up a {@link Value},
 * the value can operated after visitor
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class ValueVisitor implements SQLASTVisitor {

    private final Consumer<Value> afterVisit;

    public ValueVisitor(Consumer<Value> afterVisit) {
        this.afterVisit = afterVisit;
    }

    @Override
    public void endVisit(SQLCharExpr x) {
        String text = x.getText();
        afterVisit.accept(new ValueVarchar(text));
    }

    @Override
    public void endVisit(SQLIntegerExpr x) {
        afterVisit.accept(new ValueInt(x.getNumber().intValue()));
    }
}
