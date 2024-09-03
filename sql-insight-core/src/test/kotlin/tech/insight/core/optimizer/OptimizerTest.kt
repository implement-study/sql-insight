package tech.insight.core.optimizer

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tech.insight.core.dropDb
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.exception.UnknownColumnException
import tech.insight.core.plan.ExecutionPlan
import tech.insight.share.data.createDatabase
import tech.insight.share.data.createTable


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class OptimizerTest {

    private val dbName = "test_db"

    private val tableName = "test_table"

    @BeforeEach
    @AfterEach
    fun clear() {
        dropDb(dbName)
    }

    @Test
    fun selectUnknownCol() {
        SqlPipeline.executeSql(createDatabase(dbName))
        SqlPipeline.executeSql(createTable(tableName, dbName, "", false))
        val sql = """
            select * from $dbName.$tableName 
            where 
            (aaa = 1 or ddd = 2) and (bbb = 2 or ccc = 3)
        """.trimIndent()
        assertThrows<UnknownColumnException> { optimizeSql(sql) }
    }


    private fun optimizeSql(sql: String): ExecutionPlan {
        val command = SqlPipeline.analyzer.analysisSql(sql)
        return SqlPipeline.optimizer.optimize(command)
    }

}
