package org.gongxuanzhang.mysql.service.executor.ddl.table

import com.alibaba.fastjson2.JSONObject
import org.gongxuanzhang.mysql.core.result.SelectResult
import org.gongxuanzhang.mysql.doSql
import org.gongxuanzhang.mysql.exception.MySQLException
import org.gongxuanzhang.mysql.service.executor.ddl.database.CreateDatabaseTest
import org.gongxuanzhang.mysql.service.executor.ddl.database.DropDatabaseTest
import org.gongxuanzhang.mysql.service.executor.ddl.database.UseDatabaseTest
import org.gongxuanzhang.mysql.tool.toJSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@SpringBootTest
class DescTableTest {


    @Test
    fun descTable() {
        val database = "create_test_database"
        val tableName = "create_test_table"
        CreateDatabaseTest().doCreateDatabase(database)
        CreateTableTest().doCreateTable("create_test_database", tableName)
        try {
            arrayOf("desc $database.$tableName", "describe $database.$tableName").forEach {
                checkInfo(it)
            }
        } finally {
            DropDatabaseTest().doDropDatabase(database)
        }
    }

    @Test
    fun descSessionTable() {
        val database = "create_test_database"
        val tableName = "create_test_table"
        CreateDatabaseTest().doCreateDatabase(database)
        UseDatabaseTest().doUseDatabase(database)
        CreateTableTest().doCreateTable("create_test_database", tableName)
        try {
            arrayOf("desc $tableName", "describe $tableName").forEach {
                checkInfo(it)
            }
        } finally {
            DropDatabaseTest().doDropDatabase(database)
        }
    }

    @Test
    fun descNoExistTable() {
        assertThrows<MySQLException> { "desc aa.asdfasdf".doSql() }
    }

    fun checkInfo(sql: String) {
        val doSql = sql.doSql()
        val colId = col("id", "int").fluentPut("primary key", "true").fluentPut("auto_increment", "true")
        val colName = col("name", "varchar").fluentPut("notNull", "false")
        val colGender = col("gender", "varchar").fluentPut("notNull", "false").fluentPut("default", "张三")
        val colAge = col("age", "int").fluentPut("comment", "年龄")
        val colIdCard = col("id_card", "varchar").fluentPut("unique", "true")
        val data = (doSql as SelectResult).data
        assert(colId == data[0])
        assert(colName == data[1])
        assert(colGender == data[2])
        assert(colAge == data[3])
        assert(colIdCard == data[4])
    }

    fun col(colName: String, type: String): JSONObject {
        return mapOf(
            "field" to colName,
            "type" to type,
            "notNull" to "true",
            "primary key" to "false",
            "default" to null,
            "auto_increment" to "false",
            "unique" to "false",
            "comment" to null,
        ).toJSONObject()
    }


}
