package tech.insight.engine.innodb.execute

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.result.SelectResult
import tech.insight.engine.innodb.dropDb
import tech.insight.share.data.insertDataCount
import tech.insight.share.data.selectAll


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
