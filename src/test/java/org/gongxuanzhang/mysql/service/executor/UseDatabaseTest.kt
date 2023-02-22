package org.gongxuanzhang.mysql.service.executor

import org.gongxuanzhang.mysql.connection.Connection
import org.gongxuanzhang.mysql.core.SessionManager
import org.gongxuanzhang.mysql.service.executor.ddl.create.CreateDatabaseExecutorTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class UseDatabaseTest {

    var createDatabaseExecutorTest: CreateDatabaseExecutorTest = CreateDatabaseExecutorTest()

    /**
     * 创建数据库
     */
    @Test
    fun testUse(@Autowired connection: Connection) {
        val testDatabase1 = "aaa"
        val testDatabase2 = "bbb"
        createDatabase(connection, testDatabase1)
        connection.execute("use $testDatabase1")
        assert(diffCurrentDatabase(testDatabase1))

        createDatabase(connection, testDatabase2)
        connection.execute("use $testDatabase2")
        assert(diffCurrentDatabase(testDatabase2))

        deleteDb(testDatabase1)
        deleteDb(testDatabase2)
    }

    fun diffCurrentDatabase(expect: String): Boolean = SessionManager.currentSession().database == expect

    fun createDatabase(connection: Connection, database: String) {
        createDatabaseExecutorTest.setTestDatabaseName(database).createDatabase(connection)
    }

    fun deleteDb(database: String) = this.createDatabaseExecutorTest.setTestDatabaseName(database).deleteDb()

}
