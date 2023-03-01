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

package org.gongxuanzhang.mysql.service.token;


import org.gongxuanzhang.mysql.exception.SqlParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * sql 词法分析器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SqlTokenizer {


    private final char[] charArray;

    private int offset;

    private final int length;

    private final List<SqlToken> sqlTokenList = new ArrayList<>();


    /**
     * 给表达式加最后一个字符保证在查看next偏移的时候不会出现数组溢出
     **/
    public SqlTokenizer(String expression) {
        this.charArray = (expression + "\0").toCharArray();
        this.length = this.charArray.length;
        this.offset = 0;
    }

    /**
     * 执行词法分析
     **/
    public List<SqlToken> process() throws SqlParseException {
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
                    case '（':
                        pushOneToken(TokenKind.LEFT_PAREN);
                        break;
                    case ')':
                    case '）':
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
                    case ',':
                        pushOneToken(TokenKind.COMMA);
                        break;
                    case '.':
                        pushOneToken(TokenKind.DOT);
                        break;
                    case '!':
                        appendNe();
                        break;
                    //  todo  下划线 和$
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
                        throw new SqlParseException(c + "不能解析,请注意字符串需要用单引号");
                }
            }
        }
        return this.sqlTokenList.stream().map(TokenSupport::swapKeyword).collect(Collectors.toList());
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
            pushOneToken(TokenKind.LT);
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
        String data = new String(charArray, start, offset - start);
        this.sqlTokenList.add(new SqlToken(TokenKind.NUMBER, data));
    }

    private void appendString() throws SqlParseException {
        int start = offset;
        boolean found = false;
        while (!isFinish()) {
            offset++;
            if (currentChar() == '\'') {
                offset++;
                found = true;
                break;
            }
        }
        if (!found) {
            throw new SqlParseException("\"'\"无法解析");
        }
        String data = new String(charArray, start + 1, offset - start - 2);
        this.sqlTokenList.add(new SqlToken(TokenKind.LITERACY, data));
    }

    private void appendLiteracy() {
        int start = offset;
        do {
            offset++;
        }
        while (TokenSupport.isIdentifier(currentChar()));
        String data = new String(charArray, start, offset - start);
        this.sqlTokenList.add(new SqlToken(TokenKind.VAR, data));
    }


    private void pushOneToken(TokenKind tokenKind) {
        this.sqlTokenList.add(new SqlToken(tokenKind, currentChar() + ""));
        offset++;
    }

    private void pushTwoToken(TokenKind tokenKind) {
        this.sqlTokenList.add(new SqlToken(tokenKind, new String(charArray, offset, 2)));
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
