package tech.insight.core.optimizer

import org.junit.jupiter.api.Test
import tech.insight.core.createDatabase
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.environment.DefaultProperty
import tech.insight.core.environment.GlobalContext
import tech.insight.core.testDb
import java.io.File


class ExecutePlanTest{
    @Test
    fun createDatabaseTest(){
        val result = SqlPipeline.doSql(createDatabase)
        println(result)
        assert(File(GlobalContext[DefaultProperty.DATA_DIR],testDb).exists())
    }
}
