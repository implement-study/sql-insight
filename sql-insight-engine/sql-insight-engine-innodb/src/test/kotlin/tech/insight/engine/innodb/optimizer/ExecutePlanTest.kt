package tech.insight.core.optimizer

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
import tech.insight.share.data.*

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
        val idCol = table.columnList[0]
        assertEquals("id", idCol.name)
        assertEquals(DataType.INT, idCol.dataType)
        assertEquals(DataType.INT.defaultLength, idCol.length)
        assert(idCol.autoIncrement)
        assert(idCol.notNull)
        assert(idCol.primaryKey)
        assert(idCol.unique)
        assertEquals(ValueNull, idCol.defaultValue)
        val nameCol = table.columnList[1]
        assertEquals("name", nameCol.name)
        assertEquals(DataType.VARCHAR, nameCol.dataType)
        assertEquals(DataType.VARCHAR.defaultLength, nameCol.length)
        assert(nameCol.variable)
        assert(nameCol.notNull)
        val genderCol = table.columnList[2]
        assertEquals("gender", genderCol.name)
        assertEquals(DataType.VARCHAR, genderCol.dataType)
        assertEquals(20, genderCol.length)
        assert(genderCol.variable)
        assert(genderCol.notNull)
        assertEquals(ValueVarchar("男"), genderCol.defaultValue)
        assertEquals("性别", genderCol.comment)
        val idCardCol = table.columnList[3]
        assertEquals(DataType.CHAR, idCardCol.dataType)
        assertEquals("id_card", idCardCol.name)
        assertEquals(DataType.CHAR.defaultLength, idCardCol.length)
        assert(idCardCol.unique)
    }
}
