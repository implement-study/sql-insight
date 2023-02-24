package org.gongxuanzhang.mysql.service.executor.ddl.database

import org.gongxuanzhang.mysql.doSql
import org.gongxuanzhang.mysql.exception.ExecuteException
import org.gongxuanzhang.mysql.tool.Context
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import java.io.File


/**
 * @author gongxuanzhang
 */
@SpringBootTest
class CreateDatabaseTest {

    var database: String = "testDatabase"

    @Test
    @DisplayName("普通创建数据库")
    fun simpleCreateDatabase() {
        doCreateDatabase(database)
        val home = Context.getHome()
        val file = File(home, database)
        assert(file.exists())
        file.deleteRecursively()
    }

    fun doCreateDatabase(database: String) {
        "create database $database".doSql()
    }


    @Test
    @DisplayName("创建已经存在的数据库")
    fun createExistDatabase() {
        val file = File(Context.getHome(), this.database)
        file.mkdirs()
        assertThrows<ExecuteException> {
            "create database $database".doSql()
        }
        file.deleteRecursively()


    }

}
