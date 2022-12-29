package org.gongxuanzhang.mysql.service.token;

/**
 * token 辅助类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class TokenSupport {

    private static final int FLAG_LENGTH = 'z' + 1;

    private static final byte[] FLAGS = new byte[FLAG_LENGTH];

    private static final byte DIGIT = 0x01;

    private static final byte ALPHABET = 0x02;

    private static final byte IDENTIFIER = 0x04;


    static {
        for (int ch = '0'; ch <= '9'; ch++) {
            FLAGS[ch] |= DIGIT | IDENTIFIER;
        }
        for (int ch = 'A'; ch <= 'Z'; ch++) {
            FLAGS[ch] |= ALPHABET | IDENTIFIER;
        }
        for (int ch = 'a'; ch <= 'z'; ch++) {
            FLAGS[ch] |= ALPHABET | IDENTIFIER;
        }
        FLAGS['_'] |= IDENTIFIER;
        FLAGS['$'] |= IDENTIFIER;
    }

    /**
     * 判断字符是否是数字
     *
     * @param c char
     * @return true是数字
     **/
    public static boolean isDigit(char c) {
        if (c >= FLAG_LENGTH) {
            return false;
        }
        return (FLAGS[c] & DIGIT) != 0;
    }


    /**
     * @param c char
     * @return true是字母
     **/
    public static boolean isAlphabet(char c) {
        if (c >= FLAG_LENGTH) {
            return false;
        }
        return (FLAGS[c] & ALPHABET) != 0;
    }


    /**
     * 是否是标识符
     * @param c char
     * @return true是标识符
     **/
    public static boolean isIdentifier(char c){
        if (c >= FLAG_LENGTH) {
            return false;
        }
        return (FLAGS[c] & IDENTIFIER) != 0;
    }

}
