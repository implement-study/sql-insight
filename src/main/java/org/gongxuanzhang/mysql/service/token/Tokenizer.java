package org.gongxuanzhang.mysql.service.token;


import org.gongxuanzhang.mysql.exception.SqlParseException;

import java.util.ArrayList;
import java.util.List;


/**
 * 词法分析器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class Tokenizer {


    private final String expressionString;

    private final char[] charArray;

    private int offset;

    private final int length;

    private final List<Token> tokenList = new ArrayList<>();


    /**
     * 给表达式加最后一个字符保证在查看next偏移的时候不会出现数组溢出
     **/
    public Tokenizer(String expression) {
        this.expressionString = expression;
        this.charArray = (expression + "\0").toCharArray();
        this.length = this.charArray.length;
        this.offset = 0;
    }

    /**
     * 执行词法分析
     **/
    public List<Token> process() throws SqlParseException {
        while (offset < length) {
            char c = charArray[offset];
            //  字母直接输出
            if (TokenSupport.isAlphabet(c)) {
                appendLiteracy();
            } else {
                switch (c) {
                    case '\'':
                        appendString();
                        break;
                    case '(':
                        pushOneToken(TokenKind.LEFT_PAREN);
                        break;
                    case ')':
                        pushOneToken(TokenKind.RIGHT_PAREN);
                        break;
                    case ' ':
                    case '\t':
                    case '\r':
                    case '\n':
                    case '\0':
                        this.offset++;
                        break;
                    default:
                        throw new SqlParseException(c + "不能解析");
                }
            }


        }
        return this.tokenList;
    }

    private void appendString() {
        int start = offset;
        do {
            offset++;
        }
        while (isFinish() || currentChar() == '\'');
        String data = new String(charArray, start, offset);
        this.tokenList.add(new Token(TokenKind.LITERACY, data));
    }

    private void appendLiteracy() {
        int start = offset;
        do {
            offset++;
        }
        while (TokenSupport.isIdentifier(currentChar()));
        String data = new String(charArray, start, offset);
        this.tokenList.add(new Token(TokenKind.VAR, data));
    }


    private void pushOneToken(TokenKind tokenKind) {
        this.tokenList.add(new Token(tokenKind, currentChar() + ""));
        offset++;
    }

    private boolean isFinish() {
        return this.offset >= this.length;
    }

    private char currentChar() {
        return charArray[offset];
    }


}
