package tech.insight.engine.innodb.execute

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.insight.core.bean.DataType
import tech.insight.core.bean.value.ValueNull
import tech.insight.core.bean.value.ValueVarchar
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.environment.DatabaseManager
import tech.insight.core.environment.TableManager
import tech.insight.core.exception.DatabaseExistsException
import tech.insight.core.result.ExceptionResult
import tech.insight.engine.innodb.dropDb
import tech.insight.share.data.createDatabase
import tech.insight.share.data.createTable
import kotlin.test.assertEquals


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class CreateTableTest : SqlTestCase {


    private val dbName = "test_db"

    private val tableName = "test_table"

    private val comment = "用户表"

    @BeforeEach
    @AfterEach
    fun clear() {
        dropDb(dbName)
    }

    @Test
    override fun correctTest() {
        SqlPipeline.executeSql(createDatabase(dbName))
        SqlPipeline.executeSql(createTable(tableName, dbName, comment, false))
        val table = TableManager.require(dbName, tableName)
        assertEquals(table.name, tableName)
        assertEquals(table.database, DatabaseManager.select(dbName))
        assertEquals(table.comment, comment)
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

    @Test
    override fun errorTest() {
        SqlPipeline.executeSql(createDatabase(dbName, false))
        val db = DatabaseManager.require(dbName)
        assertEquals(db.name, dbName)
        val result = SqlPipeline.executeSql(createDatabase(dbName, false))
        assert(result is ExceptionResult)
        assert((result as ExceptionResult).exception is DatabaseExistsException)
    }

}
