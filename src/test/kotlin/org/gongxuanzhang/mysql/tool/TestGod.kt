package org.gongxuanzhang.mysql.tool

import org.gongxuanzhang.mysql.doSql
import org.gongxuanzhang.mysql.service.executor.ddl.database.CreateDatabaseTest
import org.gongxuanzhang.mysql.service.executor.ddl.table.CreateTableTest

/**
 * 测试专用上下文
 * 必须在SpringBootTest环境中
 */
class TestGod {

    val database = "god_test_db"

    val tableName = "god_test_table"

    val fullName = "$database.$tableName"

    fun prepareGodTable() {
        CreateDatabaseTest().doCreateDatabase(database)
        CreateTableTest().doCreateTable(database, tableName)
    }

    fun clearGodTable() {
        "drop database $database".doSql()
    }

}


