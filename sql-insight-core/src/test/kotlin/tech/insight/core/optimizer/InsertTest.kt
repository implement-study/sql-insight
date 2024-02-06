package tech.insight.core.optimizer

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.insight.core.*
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.engine.json.JsonEngineSupport
import tech.insight.core.environment.TableManager


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InsertTest {


    @BeforeEach
    @AfterEach
    fun clear() {
        clearDatabase()
    }

    @Test
    fun insertRow() {
        ExecutePlanTest().createTableTest()
        SqlPipeline.doSql(insert)
        val table = TableManager.require(testDb, test_table)
        val jsonFile = JsonEngineSupport.getJsonFile(table)
        jsonFile.useLines {
            assertEquals(5, it.filter { line -> line.isNotEmpty() }.count())
        }
    }


    @Test
    fun largeInsertTest() {
        ExecutePlanTest().createTableTest()
        SqlPipeline.doSql(largeInsert)
        val table = TableManager.require(testDb, test_table)
        val jsonFile = JsonEngineSupport.getJsonFile(table)
        jsonFile.useLines {
            assertEquals(1000, it.filter { line -> line.isNotEmpty() }.count())
        }
    }

    @Test
    fun twoTimeInsertTest() {
        ExecutePlanTest().createTableTest()
        SqlPipeline.doSql(largeInsert)
        SqlPipeline.doSql(largeInsert)
        val table = TableManager.require(testDb, test_table)
        val jsonFile = JsonEngineSupport.getJsonFile(table)
        jsonFile.useLines {
            assertEquals(2000, it.filter { line -> line.isNotEmpty() }.count())
        }
    }

}
