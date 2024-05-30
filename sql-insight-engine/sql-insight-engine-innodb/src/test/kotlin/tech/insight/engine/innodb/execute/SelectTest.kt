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
import tech.insight.share.data.selectComplexWhere
import tech.insight.share.data.selectWhereId
import kotlin.test.assertEquals


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class SelectTest {

    private val dbName = "test_db"

    private val tableName = "test_table"

    @BeforeEach
    @AfterEach
    fun clear() {
        dropDb(dbName)
    }

    @Test
    fun select() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertDataCount(tableName, dbName, 15))
        val selectResult = SqlPipeline.executeSql(selectAll(tableName, dbName))
        assert(selectResult is SelectResult)
        assert((selectResult as SelectResult).result.size == 15)
    }

    @Test
    fun selectWhere() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertDataCount(tableName, dbName, 15))
        val selectResult = SqlPipeline.executeSql(selectWhereId(4, tableName, dbName))
        assert(selectResult is SelectResult)
        assert((selectResult as SelectResult).result.size == 1)
        assertEquals(ValueInt(4), ((selectResult.result)[0] as ReadRow).getValueByColumnName("id"))
    }

    @Test
    fun selectComplexWhere() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertDataCount(tableName, dbName, 15))
        val selectResult = SqlPipeline.executeSql(selectComplexWhere(4, "a4", tableName, dbName))
        assert(selectResult is SelectResult)
        assert((selectResult as SelectResult).result.size == 1)
        assertEquals(ValueInt(4), ((selectResult.result)[0] as ReadRow).getValueByColumnName("id"))
        assertEquals(ValueVarchar("a4"), ((selectResult.result)[0] as ReadRow).getValueByColumnName("name"))
    }


}
