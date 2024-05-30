package tech.insight.core.engine

import java.io.File
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.insight.core.command.CreateDatabase
import tech.insight.core.command.CreateTable
import tech.insight.core.command.DropDatabase
import tech.insight.core.command.DropTable
import tech.insight.core.environment.DefaultProperty
import tech.insight.core.environment.GlobalContext
import tech.insight.share.data.createDatabase
import tech.insight.share.data.createTable
import tech.insight.share.data.dropDatabaseIe
import tech.insight.share.data.dropTableIe
import tech.insight.share.data.testDb
import tech.insight.share.data.test_table
import kotlin.test.assertEquals

/**
 * Make sure the analyzer is correct
 * @author gongxuanzhangmelt@gmail.com
 */
class AnalyzerTest {

    @BeforeEach
    fun prepare() {
        SqlPipeline.executeSql(createDatabase(testDb))
    }

    @Test
    fun createDatabaseTest() {

        DruidAnalyzer.analysisSql(createDatabase(testDb, true)).apply {
            require(this is CreateDatabase)
            assert(ifNotExists)
            assertEquals(dbName, testDb)
        }
        DruidAnalyzer.analysisSql(createDatabase(testDb)).apply {
            require(this is CreateDatabase)
            assert(!ifNotExists)
            assertEquals(dbName, testDb)
        }
    }


    @Test
    fun createTableTest() {
        DruidAnalyzer.analysisSql(createTable(test_table, testDb)).apply {
            require(this is CreateTable)
            assert(!this.ifNotExists)
            assertEquals(table.databaseName, testDb)
            assertEquals(table.name, test_table)
        }
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


