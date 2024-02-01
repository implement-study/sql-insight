package tech.insight.core.optimizer

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.insight.core.*
import tech.insight.core.bean.Column
import tech.insight.core.bean.DataType
import tech.insight.core.bean.value.ValueNull
import tech.insight.core.bean.value.ValueVarchar
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.environment.DatabaseManager
import tech.insight.core.environment.DefaultProperty
import tech.insight.core.environment.GlobalContext
import tech.insight.core.environment.TableManager
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNull


class ExecutePlanTest {


    @BeforeEach
    @AfterEach
    fun clear() {
        clearDatabase()
    }

    @Test
    fun createDatabaseTest() {
        SqlPipeline.doSql(createDatabase)
        assert(File(GlobalContext[DefaultProperty.DATA_DIR], testDb).exists())
        assert(DatabaseManager.require(testDb).name == testDb)
    }

    @Test
    fun dropDatabaseTest() {
        createDatabaseTest()
        SqlPipeline.doSql(dropDatabaseIe)
        assert(!File(GlobalContext[DefaultProperty.DATA_DIR], testDb).exists())
        assertNull(DatabaseManager.select(testDb))
    }

    @Test
    fun createTableTest() {
        createDatabaseTest()
        SqlPipeline.doSql(createTableIne)
        val table = TableManager.require(testDb, test_table)
        assertEquals(table.name, test_table)
        assertEquals(table.database, DatabaseManager.select(testDb))
        assertEquals(table.comment, "用户表")
        assertEquals(run {
            val column = Column()
            column.name = "id"
            column.dataType = DataType.INT
            column.length = DataType.INT.defaultLength
            column.autoIncrement = true
            column.notNull = true
            column.primaryKey = true
            column.unique = true
            column.defaultValue = ValueNull
            column
        }, table.columnList[0])
        assertEquals(run {
            val column = Column()
            column.notNull = true
            column.name = "name"
            column.variable = true
            column.dataType = DataType.VARCHAR
            column.length = DataType.VARCHAR.defaultLength
            column
        }, table.columnList[1])
        assertEquals(table.columnList[2], run {
            val column = Column()
            column.notNull = true
            column.name = "gender"
            column.variable = true
            column.dataType = DataType.VARCHAR
            column.length = 20
            column.defaultValue = ValueVarchar("男")
            column.comment = "性别"
            column
        })
        assertEquals(table.columnList[3], run {
            val column = Column()
            column.name = "id_card"
            column.dataType =  DataType.CHAR
            column.length = DataType.CHAR.defaultLength
            column.unique = true
            column
        })
    }
}
