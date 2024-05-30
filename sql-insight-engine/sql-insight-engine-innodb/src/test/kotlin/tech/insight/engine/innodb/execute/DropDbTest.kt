package tech.insight.engine.innodb.execute

import java.io.File
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.environment.DatabaseManager
import tech.insight.core.environment.DefaultProperty
import tech.insight.core.environment.GlobalContext
import tech.insight.core.exception.DatabaseNotExistsException
import tech.insight.engine.innodb.assertSqlThrows
import tech.insight.engine.innodb.dropDb
import tech.insight.share.data.dropDatabase
import kotlin.test.assertNull


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DropDbTest : SqlTestCase {


    private val dbName = "test_db"

    @BeforeEach
    @AfterEach
    fun clear() {
        dropDb(dbName)
    }

    @Test
    override fun correctTest() {
        CreateDbTest().correctTest()
        SqlPipeline.executeSql(dropDatabase(dbName))
        assert(!File(GlobalContext[DefaultProperty.DATA_DIR], dbName).exists())
        assertNull(DatabaseManager.select(dbName))
    }

    @Test
    override fun errorTest() {
        assertSqlThrows<DatabaseNotExistsException> { SqlPipeline.executeSql(dropDatabase(dbName)) }
    }

}
