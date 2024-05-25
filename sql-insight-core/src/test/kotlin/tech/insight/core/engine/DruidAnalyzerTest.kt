package tech.insight.core.engine

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.insight.core.command.CreateDatabase
import tech.insight.core.command.CreateTable
import tech.insight.core.command.DropDatabase
import tech.insight.core.command.DropTable
import tech.insight.core.environment.DefaultProperty
import tech.insight.core.environment.GlobalContext
import tech.insight.share.data.*
import java.io.File


class DruidAnalyzerTest {

    @BeforeEach
    fun prepare() {
        prepareDatabase()
    }

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
        val command = DruidAnalyzer.analysisSql(dropDatabaseIe)
        check(command is DropDatabase)
        assert(command.ifIsExists)
        assert(command.databaseName == testDb)
        assert(!File(GlobalContext[DefaultProperty.DATA_DIR], testDb).exists())
    }

    @Test
    fun dropTableTest() {
        val command = DruidAnalyzer.analysisSql(dropTableIe)
        check(command is DropTable)
        assert(command.ifExists)
        assert(command.dropTables == listOf(test_table))
        assert(command.dropTables[0].databaseName == testDb)
    }

}


