package org.gongxuanzhang.mysql.service.token;

import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.junit.jupiter.api.Test;

import java.util.List;


class SqlTokenizerTest {

    @Test
    public void tokenizer() {
        SqlTokenizer sqlTokenizer = new SqlTokenizer("select * from aa");
        try {
            List<SqlToken> process = sqlTokenizer.process();
            System.out.println(process);
        } catch (SqlParseException e) {
            e.printStackTrace();
        }
    }

}
