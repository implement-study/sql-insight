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

package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;

/**
 * 异常工具类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class ExceptionThrower {

    private ExceptionThrower() {

    }

    /**
     * 词法解析错误
     **/
    public static void throwSqlAnalysis(String var) throws SqlAnalysisException {
        String message = "词法解析中[%s] 解析过程出现错误";
        throw new SqlAnalysisException(String.format(message, var));
    }

    /**
     * 语法解析错误
     **/
    public static void throwSqlParse(String var) throws SqlParseException {
        String message = "语法解析中[%s] 解析过程出现错误";
        throw new SqlParseException(String.format(message, var));
    }


    public static void errorToken(SqlToken sqlToken) throws SqlAnalysisException {
        throw new SqlAnalysisException(sqlToken.getValue() + "解析异常");
    }

    public static void expectToken(SqlToken sqlToken, TokenKind expect) throws SqlAnalysisException {
        if (sqlToken.getTokenKind() != expect) {
            errorToken(sqlToken);
        }
    }

    public static void ifNotThrow(boolean expression, String message) throws SqlAnalysisException {
        if (!expression) {
            throw new SqlAnalysisException(message);
        }
    }

    public static void ifNotThrow(boolean expression, SqlToken sqlToken) throws SqlAnalysisException {
        ifNotThrow(expression, String.format("sql有错误,%s无法解析", sqlToken.getValue()));
    }

    public static void ifNotThrow(boolean expression) throws SqlAnalysisException {
        ifNotThrow(expression, "sql有错误无法解析");
    }

    /**
     * 异常转换
     *
     * @param e 异常
     *
     * @return 其实没有返回值，只是为了适配编译
     **/
    public static <V> V errorSwap(Exception e) throws MySQLException {
        if (e instanceof MySQLException) {
            throw (MySQLException) e;
        } else {
            e.printStackTrace();
            throw new MySQLException(e.getMessage());
        }
    }
}
