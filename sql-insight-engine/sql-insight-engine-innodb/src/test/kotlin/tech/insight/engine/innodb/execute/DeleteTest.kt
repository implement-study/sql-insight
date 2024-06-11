package tech.insight.engine.innodb.execute

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.insight.core.bean.ReadRow
import tech.insight.core.bean.value.ValueInt
import tech.insight.core.bean.value.ValueVarchar
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.result.SelectResult
import tech.insight.engine.innodb.dropDb
import tech.insight.share.data.insertDataCount
import tech.insight.share.data.selectAll
import tech.insight.share.data.selectIdNameWhere
import tech.insight.share.data.selectWhereId
import kotlin.test.assertEquals


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DeleteTest  {

    private val dbName = "test_db"

    private val tableName = "test_table"

    @BeforeEach
    @AfterEach
    fun clear() {
        dropDb(dbName)
    }

    @Test
    fun deleteOne() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertDataCount(tableName, dbName, 15))
        SqlPipeline.executeSql("delete from $dbName.$tableName where id = 1")
        val selectResult = SqlPipeline.executeSql(selectAll(tableName, dbName))
        assert((selectResult as SelectResult).result.size == 14)
    }

    @Test
    fun deleteWhere() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertDataCount(tableName, dbName, 15))
        SqlPipeline.executeSql("delete from $dbName.$tableName where id > 1")
        val selectResult = SqlPipeline.executeSql(selectAll(tableName, dbName))
        assert((selectResult as SelectResult).result.size == 1)
    }


}
