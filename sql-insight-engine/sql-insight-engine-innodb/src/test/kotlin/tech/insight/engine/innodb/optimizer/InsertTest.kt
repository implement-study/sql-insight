//package tech.insight.engine.innodb.optimizer
//
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertDoesNotThrow
//import tech.insight.core.engine.SqlPipeline
//import tech.insight.core.engine.json.JsonEngineSupport
//import tech.insight.core.environment.TableManager
//import tech.insight.core.optimizer.ExecutePlanTest
//import tech.insight.share.data.*
//
//
///**
// * @author gxz gongxuanzhangmelt@gmail.com
// **/
//class InsertTest {
//
//
//    @BeforeEach
//    @AfterEach
//    fun clear() {
//        clearDatabase()
//    }
//
//
//    @Test
//    fun insertRow() {
//        ExecutePlanTest().createTableTest()
//        SqlPipeline.executeSql(insert)
//        assertDoesNotThrow { TableManager.require(testDb, test_table) }
//    }
//
//
//    @Test
//    fun largeInsertTest() {
//        ExecutePlanTest().createTableTest()
//        SqlPipeline.executeSql(largeInsert)
//        val table = TableManager.require(testDb, test_table)
//        val jsonFile = JsonEngineSupport.getJsonFile(table)
//        jsonFile.useLines {
//            assertEquals(1000, it.filter { line -> line.isNotEmpty() }.count())
//        }
//    }
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
//
//}
//
