package org.gongxuanzhang.mysql.service.token;


/**
 * 词法token
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class Token {

    private final TokenKind tokenKind;

    private final String value;

    public Token(TokenKind tokenKind, String value) {
        this.tokenKind = tokenKind;
        this.value = value;
    }


}
