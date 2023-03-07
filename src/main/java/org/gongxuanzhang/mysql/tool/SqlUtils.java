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

import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import org.gongxuanzhang.mysql.core.FromBox;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.core.select.From;
import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
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

    private static final String TABLENAME_SEPARATOR = ".";


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
     * 拼装已经存在的表
     **/
    public static void assembleTableInfo(FromBox box, SQLTableSource tableSource) throws MySQLException {
        TableInfo tableInfo = selectTableInfo(tableSource);
        box.setFrom(new From(tableInfo));
    }

    /**
     * 选择已经存在的表信息
     *
     * @param tableSource sql解析出的表信息
     * @return 返回已经存在的表信息
     **/
    public static TableInfo selectTableInfo(SQLTableSource tableSource) throws MySQLException {
        String tableSourceStr = tableSource.toString();
        if (tableSourceStr.indexOf(TABLENAME_SEPARATOR) != tableSourceStr.lastIndexOf(TABLENAME_SEPARATOR)) {
            throw new MySQLException(tableSource.toString() + "无法解析");
        }
        if (tableSourceStr.contains(TABLENAME_SEPARATOR)) {
            String[] split = tableSourceStr.split("\\.");
            return Context.getTableManager().select(split[0] + TABLENAME_SEPARATOR + split[1]);
        }
        DatabaseInfo database = SessionManager.currentSession().getDatabase();
        String absoluteName = database + TABLENAME_SEPARATOR + tableSourceStr;
        return Context.getTableManager().select(absoluteName);
    }

    /**
     * 填充tableInfo
     * 用于还不存在的表
     **/
    public static void fillTableInfo(TableInfo tableInfo, SQLTableSource tableSource) throws MySQLException {
        String candidate = tableSource.toString();
        if (candidate.indexOf(TABLENAME_SEPARATOR) != candidate.lastIndexOf(TABLENAME_SEPARATOR)) {
            throw new MySQLException(tableSource + "无法解析");
        }
        DatabaseInfo databaseInfo;
        String tableName;
        if (candidate.contains(TABLENAME_SEPARATOR)) {
            String[] split = candidate.split("\\.");
            databaseInfo = new DatabaseInfo(split[0]);
            tableName = split[1];
        } else {
            databaseInfo = SessionManager.currentSession().getDatabase();
            tableName = candidate;
        }
        tableInfo.setDatabase(databaseInfo);
        tableInfo.setTableName(tableName);
    }

    /**
     * 批量查询已经存在的表信息
     *
     * @param tableSources sql解析出的表信息
     * @return 返回填充你
     **/
    public static List<TableInfo> batchSelectTableInfo(List<? extends SQLTableSource> tableSources) throws MySQLException {
        if (CollectionUtils.isEmpty(tableSources)) {
            return new ArrayList<>();
        }
        List<TableInfo> result = new ArrayList<>(tableSources.size());
        for (SQLTableSource tableSource : tableSources) {
            result.add(selectTableInfo(tableSource));
        }
        return result;
    }


}
