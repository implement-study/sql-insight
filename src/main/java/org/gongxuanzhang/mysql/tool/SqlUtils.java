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

package org.gongxuanzhang.mysql.tool;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import org.gongxuanzhang.mysql.entity.Cell;
import org.gongxuanzhang.mysql.entity.IntCell;
import org.gongxuanzhang.mysql.entity.NullCell;
import org.gongxuanzhang.mysql.entity.VarcharCell;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.SqlParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql解析相关的工具类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SqlUtils {

    private SqlUtils() {

    }

    private static final Pattern ILLEGAL_PATTERN = Pattern.compile("[^\\w]+");

    private static final char BACKTICK = '`';
    private static final char DOUBLE_QUOTE = '"';
    private static final char SINGLE_QUOTE = '\'';


    /**
     * 校验变量名的合法行
     *
     * @param varName 变量名  可以是表名，变量名，数据库名等
     **/
    public static void checkVarName(String varName) throws SqlParseException {
        Matcher matcher = ILLEGAL_PATTERN.matcher(varName);
        if (matcher.find()) {
            throw new SqlParseException("变量名[" + varName + "]非法,只能有字母数字下划线");
        }
    }


    /**
     * 计算器执行时间
     *
     * @param startTime 开始时间
     * @return double小数的秒
     **/
    public static String sqlTime(long startTime) {
        return String.format("%.3f s", (System.currentTimeMillis() - startTime) / 1000.0);
    }

    /**
     * 解析转义字符
     * 修剪前后的` " '  sql转义字符
     * 不支持多级转义
     *
     * @return 返回修剪之后的
     **/
    public static String trimSqlEsc(String str) {
        if (str.length() <= 2) {
            return str;
        }
        switch (str.charAt(0)) {
            case BACKTICK: {
                if (str.charAt(str.length() - 1) == BACKTICK) {
                    return str.substring(1, str.length() - 1);
                }
                return str;
            }
            case DOUBLE_QUOTE:
                if (str.charAt(str.length() - 1) == DOUBLE_QUOTE) {
                    return str.substring(1, str.length() - 1);
                }
                return str;
            case SINGLE_QUOTE:
                if (str.charAt(str.length() - 1) == SINGLE_QUOTE) {
                    return str.substring(1, str.length() - 1);
                }
                return str;
            default:
                return str;
        }
    }

    public static Cell<?> cellWrap(SQLExpr sqlExpr) throws MySQLException {
        if (sqlExpr instanceof SQLIntegerExpr) {
            return new IntCell((Integer) ((SQLIntegerExpr) sqlExpr).getValue());
        } else if (sqlExpr instanceof SQLCharExpr) {
            return new VarcharCell(((SQLCharExpr) sqlExpr).getText());
        } else if (sqlExpr instanceof SQLNullExpr) {
            return new NullCell();
        } else {
            throw new MySQLException(sqlExpr.toString() + "暂不支持");
        }
    }

}
