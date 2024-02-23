package tech.insight.core.optimizer

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.engine.json.JsonEngineSupport
import tech.insight.core.environment.TableManager
import tech.insight.share.test.*


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DeleteTest {


    @BeforeEach
    @AfterEach
    fun clear() {
        clearDatabase()
    }

    @Test
    fun deleteRow() {
        ExecutePlanTest().createTableTest()
        SqlPipeline.doSql(insert)
        SqlPipeline.doSql(deleteRemain1)
        val table = TableManager.require(testDb, test_table)
    }


    @Test
    fun deleteLargeInsertTest() {
        ExecutePlanTest().createTableTest()
        SqlPipeline.doSql(largeInsert)
        SqlPipeline.doSql(deleteRemain100)
        val table = TableManager.require(testDb, test_table)
        val jsonFile = JsonEngineSupport.getJsonFile(table)
        jsonFile.useLines {
            assertEquals(100, it.filter { line -> line.isNotEmpty() }.count())
        }
    }


}
