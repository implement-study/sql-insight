package org.gongxuanzhang.mysql.service.token;


import lombok.Data;

/**
 * 词法token
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class SqlToken {

    private final TokenKind tokenKind;

    private final String value;

    public SqlToken(TokenKind tokenKind, String value) {
        this.tokenKind = tokenKind;
        this.value = value;
    }

    @Override
    public String toString() {
        return "[" + tokenKind.toString() + "] " + value;
    }
}
