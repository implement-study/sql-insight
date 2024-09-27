package tech.insight.engine.innodb.execute

import java.io.File
import java.util.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.insight.core.bean.value.ValueVarchar
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.environment.DefaultProperty
import tech.insight.core.environment.GlobalContext
import tech.insight.core.result.SelectResult
import tech.insight.engine.innodb.dropDb
import tech.insight.share.data.selectAll
import tech.insight.share.data.updateAllName


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class UpdateTest {


    private val dbName = "test_db"

    private val tableName = "test_table"


    @TempDir
    lateinit var tempDir: File

    @BeforeEach
    fun setHome(){
        GlobalContext[DefaultProperty.DATA_DIR] = tempDir.path
    }
    
    
    
    @AfterEach
    fun clear() {
        dropDb(dbName)
    }

    @Test
    fun updateTestLarge() {
        InsertTest().insertOneRow()
        val newName = UUID.randomUUID().toString()
        SqlPipeline.executeSql(updateAllName(newName, tableName, dbName))
        val selectResult = SqlPipeline.executeSql(selectAll(tableName, dbName))
        val resultRows = (selectResult as SelectResult).result
        assertEquals(1, resultRows.size)
        resultRows.forEach {
            assertEquals(ValueVarchar(newName), it.getValueByColumnName("name"))
        }
    }

    @Test
    fun updateSmallTest() {
        InsertTest().insertOneRow()
        val newName = "a"
        SqlPipeline.executeSql(updateAllName(newName, tableName, dbName))
        val selectResult = SqlPipeline.executeSql(selectAll(tableName, dbName))
        val resultRows = (selectResult as SelectResult).result
        assertEquals(1, resultRows.size)
        resultRows.forEach {
            assertEquals(ValueVarchar(newName), it.getValueByColumnName("name"))
        }
    }


}
