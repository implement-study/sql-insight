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
            if (TokenSupport.isAlphabet(c)) {
                appendLiteracy();
            } else if (TokenSupport.isDigit(c)) {
                appendDigit();
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
                    case '>':
                        appendGtOrGte();
                        break;
                    case '<':
                        appendLtOrLte();
                        break;
                    case '=':
                        pushOneToken(TokenKind.EQUALS);
                        break;
                    case '+':
                        pushOneToken(TokenKind.PLUS);
                        break;
                    case '-':
                        pushOneToken(TokenKind.MINUS);
                        break;
                    case '*':
                        pushOneToken(TokenKind.MULTI);
                        break;
                    case '/':
                        pushOneToken(TokenKind.DIVIDE);
                        break;
                    case '%':
                        pushOneToken(TokenKind.MOL);
                        break;
                    case '!':
                        appendNe();
                        break;
                    case '@':
                        appendAt();
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

    private void appendAt() {
        if (nextChar() == '@') {
            pushTwoToken(TokenKind.DOUBLE_AT);
        } else {
            pushOneToken(TokenKind.AT);
        }
    }

    private void appendNe() throws SqlParseException {
        if (nextChar() == '=') {
            pushTwoToken(TokenKind.NE);
        } else {
            throw new SqlParseException(currentChar() + "不能解析");
        }
    }

    private void appendLtOrLte() {
        if (nextChar() == '=') {
            pushTwoToken(TokenKind.LTE);
        } else {
            pushOneToken(TokenKind.LTE);
        }
    }

    private void appendGtOrGte() {
        if (nextChar() == '=') {
            pushTwoToken(TokenKind.GTE);
        } else {
            pushOneToken(TokenKind.GT);
        }
    }

    private void appendDigit() {
        int start = offset;
        do {
            offset++;
        } while (TokenSupport.isDigit(currentChar()));
        String data = new String(charArray, start, offset);
        this.tokenList.add(new Token(TokenKind.INTEGER, data));
    }

    private void appendString() throws SqlParseException {
        int start = offset;
        boolean found = false;
        while (!isFinish()) {
            offset++;
            if (currentChar() != '\'') {
                found = true;
                break;
            }
        }
        String data = new String(charArray, start, offset);
        if (!found) {
            throw new SqlParseException(data + "无法解析");
        }
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

    private void pushTwoToken(TokenKind tokenKind) {
        this.tokenList.add(new Token(tokenKind, new String(charArray, offset, offset + 1)));
        offset += 2;
    }

    private boolean isFinish() {
        return this.offset >= this.length;
    }

    private char currentChar() {
        return charArray[offset];
    }

    private char nextChar() {
        return charArray[offset + 1];
    }

}
