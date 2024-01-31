package tech.insight.core.engine

import org.junit.jupiter.api.Test
import tech.insight.core.*
import tech.insight.core.command.CreateDatabase
import tech.insight.core.command.CreateTable
import tech.insight.core.command.DropDatabase
import tech.insight.core.command.DropTable


class DruidAnalyzerTest {


    @Test
    fun createDatabaseTest() {
        val command = DruidAnalyzer.analysisSql(createDatabase)
        assert(command is CreateDatabase)
        check(command is CreateDatabase)
        assert(command.ifNotExists)
        assert(command.dbName == testDb)
    }


    @Test
    fun createTableTest() {
        TODO("create database before create table")
        var createTable = DruidAnalyzer.analysisSql(createTableDine) as CreateTable
        assert(!createTable.ifNotExists)
        assert(DruidAnalyzer.analysisSql(createTableDine) is CreateTable)
        createTable = DruidAnalyzer.analysisSql(createTableIne) as CreateTable
        assert(createTable.ifNotExists)
        assert(createTable.table.databaseName == testDb)
        assert(createTable.table.name == test_table)
    }

    @Test
    fun dropDatabaseTest() {
        TODO("create database before drop database")
        val command = DruidAnalyzer.analysisSql(dropDatabaseIe)
        check(command is DropDatabase)
        assert(command.ifIsExists)
        assert(command.database.name == testDb)
    }

    @Test
    fun dropTableTest() {
        TODO("create database before drop database")
        val command = DruidAnalyzer.analysisSql(dropTableIe)
        check(command is DropTable)
        assert(command.ifExists)
        assert(command.dropTables == listOf(test_table))
        assert(command.dropTables[0].databaseName == testDb)
    }

}


