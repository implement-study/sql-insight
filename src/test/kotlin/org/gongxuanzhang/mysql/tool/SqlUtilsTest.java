package org.gongxuanzhang.mysql.tool;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SqlUtilsTest {

    @Test
    @DisplayName("格式化sql")
    public void sqlFormat() {
        String sql = "    select * from      user     where   1=1  and  name     =  4   ";
        assertEquals(SqlUtils.formatSql(sql), "select * from user where 1 = 1 and name = 4");
    }

}
