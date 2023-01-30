package org.gongxuanzhang.mysql.tool;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class CompareUtils {

    private CompareUtils() {

    }

    public static int compareString(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return 0;
        }
        if (s1 == null) {
            return 1;
        }
        if (s2 == null) {
            return -1;
        }
        return s1.compareTo(s2);
    }
}
