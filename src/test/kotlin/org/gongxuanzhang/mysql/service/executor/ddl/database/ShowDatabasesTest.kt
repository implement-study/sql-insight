package org.gongxuanzhang.mysql.service.executor.ddl.database

import org.gongxuanzhang.mysql.core.result.SingleRowResult
import org.gongxuanzhang.mysql.destructuringEquals
import org.gongxuanzhang.mysql.doSql
import org.gongxuanzhang.mysql.tool.Context
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.io.FileFilter


/**
 * @author gongxuanzhang
 */
@SpringBootTest
class ShowDatabasesTest {


    @Test
    fun showDatabases() {
        val testDatabases = arrayOf("test1", "test2", "test3")
        testDatabases.forEach {
            "create database $it".doSql()
        }
        val databases = Context.getHome().listFiles(FileFilter { it.isDirectory })
        val doSql = "show databases".doSql()
        assert((doSql as SingleRowResult).destructuringEquals(databases?.map { it.name }))
        testDatabases.forEach {
            "drop database $it".doSql()
        }
    }


}
