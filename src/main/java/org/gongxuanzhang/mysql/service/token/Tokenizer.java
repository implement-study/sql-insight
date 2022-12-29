package org.gongxuanzhang.mysql.service.token;


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


    public Tokenizer(String expression) {
        this.expressionString = expression;
        this.charArray = (expression + "\0").toCharArray();
        this.length = this.charArray.length;
        this.offset = 0;
    }

    /**
     * 执行词法分析
     **/
    public List<Token> process() {
        while (offset < length) {
            char c = charArray[offset];
            //  字母直接输出
            if (TokenSupport.isAlphabet(c)) {
                appendLiteracy();
            }
        }
        return this.tokenList;
    }

    private void appendLiteracy() {
        int start = offset;
        do {
            offset++;
        }
        while (TokenSupport.isIdentifier(charArray[offset]));
        String data = new String(charArray, start, offset);
        KeywordSearcher trie = KeywordSearcher.trie;
        TokenKind search = trie.search(data);
        this.tokenList.add(new Token(search, data));
    }





}
