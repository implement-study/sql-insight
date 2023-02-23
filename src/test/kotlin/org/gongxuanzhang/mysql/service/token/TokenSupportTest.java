package org.gongxuanzhang.mysql.service.token;

import org.gongxuanzhang.mysql.exception.MySQLException;
import org.junit.jupiter.api.Test;


class TokenSupportTest {

    @Test
    public void testChain() throws MySQLException {
        TokenSupport.token(new SqlToken(TokenKind.DOUBLE_AT, "@@")).when(TokenKind.NE).then(() -> {
            System.out.println("这是ne");
        }).when(TokenKind.DOUBLE_AT).then(() -> {
            System.out.println("这是双dou");
        }).get();
    }

}
