package tech.insight.engine.innodb.execute

import java.util.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.insight.core.engine.SqlPipeline
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

    @BeforeEach
    @AfterEach
    fun clear() {
        dropDb(dbName)
    }

    @Test
    fun updateTest() {
        InsertTest().insertOneRow()
        val newName = UUID.randomUUID().toString()
        SqlPipeline.executeSql(updateAllName(newName, tableName, dbName))
        val selectResult = SqlPipeline.executeSql(selectAll(tableName, dbName))
        val resultRows = (selectResult as SelectResult).result
        assertEquals(1, resultRows.size)
        resultRows.forEach {
            assertEquals(newName, it.getValueByColumnName("name"))
        }
    }
    
    @Test
    fun updateSmallTest() {
        InsertTest().insertOneRow()
        val newName = "a"
        val aaa = SqlPipeline.executeSql(selectAll(tableName, dbName))
        SqlPipeline.executeSql(updateAllName(newName, tableName, dbName))
        val selectResult = SqlPipeline.executeSql(selectAll(tableName, dbName))
        val resultRows = (selectResult as SelectResult).result
        assertEquals(1, resultRows.size)
        resultRows.forEach {
            assertEquals(newName, it.getValueByColumnName("name"))
        }
    }


}
