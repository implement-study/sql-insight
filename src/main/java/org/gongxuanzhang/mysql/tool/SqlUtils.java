package org.gongxuanzhang.mysql.tool;

/**
 * sql解析相关的工具类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SqlUtils {

    private SqlUtils() {

    }


    /**
     * 此方法会把sql格式化
     * 格式化包括 去掉前后空格，
     * 所有连续空格变成一个
     * 等号两边加一个空格
     *
     * @param sql 用户输入的sql
     * @return 格式化之后的sql
     **/
    public static String formatSql(String sql) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : sql.trim().toCharArray()) {
            if (c == '=') {
                stringBuilder.append(" ").append(c).append(" ");
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString().replaceAll("\\s+", " ");
    }
}
