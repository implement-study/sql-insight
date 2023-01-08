package org.gongxuanzhang.mysql.tool;

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

    /**
     * 此方法会把sql格式化
     * 格式化包括 去掉前后空格，
     * 所有连续空格变成一个
     * 等号两边加一个空格
     * 括号左右加一个空格
     *
     * @param sql 用户输入的sql
     * @return 格式化之后的sql
     **/
    @Deprecated
    public static String formatSql(String sql) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : sql.trim().toCharArray()) {
            switch (c) {
                case '=':
                case '(':
                case ')':
                    stringBuilder.append(" ").append(c).append(" ");
                    break;
                default:
                    stringBuilder.append(c);
            }
        }
        return stringBuilder.toString().replaceAll("\\s+", " ");
    }


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
        long endTime = System.currentTimeMillis();
        return String.format("%.3f s", (System.currentTimeMillis() - endTime) / 1000.0);
    }
}
