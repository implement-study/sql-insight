package tech.insight.core.engine

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.insight.core.command.CreateDatabase
import tech.insight.core.command.CreateTable
import tech.insight.core.command.DropDatabase
import tech.insight.core.command.DropTable
import tech.insight.core.dropDb
import tech.insight.core.environment.TableManager
import tech.insight.share.data.createDatabase
import tech.insight.share.data.createTable
import tech.insight.share.data.dropDatabaseIe
import tech.insight.share.data.dropTableIe
import tech.insight.share.data.testDb
import tech.insight.share.data.test_table
import kotlin.test.assertEquals

/**
 * Make sure the analyzer is correct
 *
 * @author gongxuanzhangmelt@gmail.com
 */
class AnalyzerTest {

    @BeforeEach
    fun prepare() {
        SqlPipeline.executeSql(createDatabase(testDb))
    }

    @AfterEach
    fun clean() {
        dropDb(testDb)
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
    }

    @Test
    fun dropTableTest() {
        val emptyCommand = DruidAnalyzer.analysisSql(dropTableIe)
        check(emptyCommand is DropTable)
        assert(emptyCommand.ifExists)
        assert(emptyCommand.dropTables.isEmpty())
        SqlPipeline.executeSql(createTable(test_table, testDb))
        val command = DruidAnalyzer.analysisSql(dropTableIe)
        check(command is DropTable)
        val table = TableManager.require(testDb, test_table)
        assert(command.dropTables == listOf(table))
    }
}
