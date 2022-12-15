package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.entity.ColumnInfo;
import org.gongxuanzhang.mysql.entity.ColumnType;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.tool.SqlUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertThrows;


class TableCreatorTest {

    @Test
    @DisplayName("正常建表sql解析")
    public void correctSqlAnalysis() throws SqlParseException {
        String sql = "create table user(" +
                "id int primary key auto_increment," +
                "name varchar comment 名字 ) comment=zhangsan engine=json ";
        sql = SqlUtils.formatSql(sql);
        TableCreator tableCreator = new TableCreator(sql);
        TableInfo info = tableCreator.getInfo();
        assertEquals(info.getEngine(), "json");
        assertEquals(info.getComment(), "zhangsan");
        assertEquals(info.getTableName(), "user");
        assertLinesMatch(info.getPrimaryKey(), Arrays.asList("id"));
        ColumnInfo id = new ColumnInfo();
        id.setName("id");
        id.setAutoIncrement(true);
        id.setType(ColumnType.INT);
        ColumnInfo name = new ColumnInfo();
        name.setName("name");
        name.setType(ColumnType.STRING);
        name.setComment("名字");
        assertEquals(info.getColumnInfos(),Arrays.asList(id,name));
    }

    @Test
    public void keywordError()  {
        assertThrows(SqlParseException.class,()->{
            String sql = "create table user(" +
                    "id int primary  auto_increment," +
                    "name varchar comment 名字 ) comment=zhangsan engine=json ";
            sql = SqlUtils.formatSql(sql);
            new TableCreator(sql);
        });

        assertThrows(SqlParseException.class,()->{
            String sql = "create table user(" +
                    "id int primary  auto_increment," +
                    "name varchar comment 名字 ) comment1=zhangsan engine=json ";
            sql = SqlUtils.formatSql(sql);
            new TableCreator(sql);
        });

        assertThrows(SqlParseException.class,()->{
            String sql = "create table user(" +
                    "id int primary  auto_increment," +
                    "name varchar comment 名字 ( comment1=zhangsan engine=json ";
            sql = SqlUtils.formatSql(sql);
            new TableCreator(sql);
        });
    }


}
