package tech.insight.engine.innodb.execute

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.environment.TableManager
import tech.insight.engine.innodb.dropDb
import tech.insight.engine.innodb.execute.CreateTableTest
import tech.insight.share.data.*
import kotlin.test.assertNotNull


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


    /**
     * This test case will trigger exactly dictionary split
     */
    @Test
    fun insertPageDictionarySplitPage() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertDataCount(tableName, dbName, 10))
        assertDoesNotThrow { TableManager.require(testDb, test_table) }
    }


    @Test
    fun largeInsertTest() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertDataCount(tableName, dbName, 1000))
        assertNotNull(TableManager.require(testDb, test_table))
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

