package tech.insight.engine.innodb.execute

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.environment.DatabaseManager
import tech.insight.core.environment.DefaultProperty
import tech.insight.core.environment.GlobalContext
import tech.insight.core.exception.DatabaseExistsException
import tech.insight.core.result.ExceptionResult
import tech.insight.engine.innodb.dropDb
import tech.insight.share.data.createDatabase
import java.io.File
import kotlin.test.assertEquals


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class CreateDbTest : SqlTestCase {


    private val dbName = "test_db"

    @BeforeEach
    @AfterEach
    fun clear() {
        dropDb(dbName)
    }

    @Test
    override fun correctTest() {
        SqlPipeline.executeSql(createDatabase(dbName, false))
        val db = DatabaseManager.require(dbName)
        assert(File(GlobalContext[DefaultProperty.DATA_DIR], dbName).exists())
        assertEquals(db.name, dbName)
    }

    @Test
    override fun errorTest() {
        SqlPipeline.executeSql(createDatabase(dbName, false))
        var db = DatabaseManager.require(dbName)
        assertEquals(db.name, dbName)
        val result = SqlPipeline.executeSql(createDatabase(dbName, false))
        assert(result is ExceptionResult)
        assert((result as ExceptionResult).exception is DatabaseExistsException)
        db = DatabaseManager.require(dbName)
        assertEquals(db.name, dbName)
    }

}
