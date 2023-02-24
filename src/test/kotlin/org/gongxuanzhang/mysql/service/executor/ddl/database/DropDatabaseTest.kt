package org.gongxuanzhang.mysql.service.executor.ddl.database

import org.gongxuanzhang.mysql.core.result.Result
import org.gongxuanzhang.mysql.doSql
import org.gongxuanzhang.mysql.exception.MySQLException
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
class DropDatabaseTest {

    var database: String = "testDatabase"

    @Test
    @DisplayName("普通删除数据库")
    fun simpleDropDatabase() {
        val dir = File(Context.getHome(), database)
        dir.mkdirs()
        assert(dir.exists() && dir.isDirectory)
        doDropDatabase(database)
        assert(!dir.exists())
    }


    fun doDropDatabase(database: String): Result {
        return "drop database $database".doSql()
    }


    @Test
    @DisplayName("删除不存在的数据库")
    fun dropNoExist() {
        val dir = File(Context.getHome(), database)
        assert(!dir.exists())
        assertThrows<MySQLException> {
            "drop database $database".doSql()
        }

    }

}
