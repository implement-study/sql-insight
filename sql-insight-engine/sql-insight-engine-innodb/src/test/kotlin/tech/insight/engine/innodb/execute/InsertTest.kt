package tech.insight.engine.innodb.optimizer

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.engine.json.JsonEngineSupport
import tech.insight.core.environment.TableManager
import tech.insight.engine.innodb.dropDb
import tech.insight.engine.innodb.execute.CreateTableTest
import tech.insight.share.data.insertData
import tech.insight.share.data.largeInsert
import tech.insight.share.data.testDb
import tech.insight.share.data.test_table


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InsertTest {


    private val dbName = "test_db"

    private val tableName = "test_table"

    @BeforeEach
    @AfterEach
    fun clear() {
        dropDb(dbName)
    }

    @Test
    fun insertRow() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertData(tableName, dbName))
        assertDoesNotThrow { TableManager.require(testDb, test_table) }
    }


    @Test
    fun largeInsertTest() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(largeInsert)
        val table = TableManager.require(testDb, test_table)
        val jsonFile = JsonEngineSupport.getJsonFile(table)
        jsonFile.useLines {
            assertEquals(1000, it.count { line -> line.isNotEmpty() })
        }
    }
    //
    //    @Test
    //    fun twoTimeInsertTest() {
    //        ExecutePlanTest().createTableTest()
    //        SqlPipeline.executeSql(largeInsert)
    //        SqlPipeline.executeSql(largeInsert)
    //        val table = TableManager.require(testDb, test_table)
    //        val jsonFile = JsonEngineSupport.getJsonFile(table)
    //        jsonFile.useLines {
    //            assertEquals(2000, it.filter { line -> line.isNotEmpty() }.count())
    //        }
    //    }

}

