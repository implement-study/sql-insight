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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLValuableExpr;


/**
 * where 条件
 * todo  条件还不支持
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public class Where {

    private final SQLExpr source;

    public Where(SQLExpr whereExpr) {
        this.source = whereExpr;
    }


    /**
     * 装配where
     *
     * @param whereExpr 解析出的表达式 可以是null
     **/
    public static void assembleWhere(SQLExpr whereExpr) {
        if (whereExpr == null) {
            return;
        }

        if (whereExpr instanceof SQLValuableExpr) {
            ((SQLValuableExpr) whereExpr).getValue();

        }

        if (whereExpr instanceof SQLBinaryOpExpr) {

        }


    }
}
