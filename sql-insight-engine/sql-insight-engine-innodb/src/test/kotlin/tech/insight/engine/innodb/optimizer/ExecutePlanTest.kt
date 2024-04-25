//package tech.insight.core.optimizer
//
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import tech.insight.core.bean.DataType
//import tech.insight.core.bean.value.ValueNull
//import tech.insight.core.bean.value.ValueVarchar
//import tech.insight.core.engine.SqlPipeline
//import tech.insight.core.environment.DatabaseManager
//import tech.insight.core.environment.DefaultProperty
//import tech.insight.core.environment.GlobalContext
//import tech.insight.core.environment.TableManager
//import java.io.File
//import kotlin.test.assertEquals
//import kotlin.test.assertNull
//import tech.insight.share.data.*
//
//class ExecutePlanTest {
//
//
//    @BeforeEach
//    @AfterEach
//    fun clear() {
//        clearDatabase()
//    }
//
//    @Test
//    fun createDatabaseTest() {
//        SqlPipeline.executeSql(createDatabase)
//        assert(File(GlobalContext[DefaultProperty.DATA_DIR], testDb).exists())
//        assert(DatabaseManager.require(testDb).name == testDb)
//    }
//
//    @Test
//    fun dropDatabaseTest() {
//        createDatabaseTest()
//        SqlPipeline.executeSql(dropDatabaseIe)
//        assert(!File(GlobalContext[DefaultProperty.DATA_DIR], testDb).exists())
//        assertNull(DatabaseManager.select(testDb))
//    }
//
//}
