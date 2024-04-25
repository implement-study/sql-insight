//package tech.insight.core.optimizer
//
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import tech.insight.core.engine.SqlPipeline
//import tech.insight.core.environment.TableManager
//import tech.insight.share.data.*
//
//
///**
// * @author gxz gongxuanzhangmelt@gmail.com
// **/
//class SelectTest {
//
//
//    @BeforeEach
//    @AfterEach
//    fun clear() {
//        clearDatabase()
//    }
//
//    @Test
//    fun select() {
//        ExecutePlanTest().createTableTest()
//        SqlPipeline.executeSql(largeInsert)
//        val table = TableManager.require(testDb, test_table)
//        val result = SqlPipeline.executeSql(select)
//        println(result)
//    }
//
//
//}
