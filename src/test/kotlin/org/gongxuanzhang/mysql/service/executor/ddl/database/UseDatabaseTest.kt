package org.gongxuanzhang.mysql.service.executor.ddl.database

import org.gongxuanzhang.mysql.core.SessionManager
import org.gongxuanzhang.mysql.doSql
import org.gongxuanzhang.mysql.exception.MySQLException
import org.gongxuanzhang.mysql.tool.randomDatabase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@SpringBootTest
class UseDatabaseTest {


    @Test
    fun useDatabase() {
        val database = randomDatabase()
        "create database '$database'".doSql()
        "use $database".doSql()
        assert(SessionManager.currentSession().database == database)
        "drop database $database".doSql()
    }

    @Test
    fun userNoExistDatabase() {
        val uuid = randomDatabase()
        assertThrows<MySQLException> {
            "use $uuid".doSql()
        }
    }
}
