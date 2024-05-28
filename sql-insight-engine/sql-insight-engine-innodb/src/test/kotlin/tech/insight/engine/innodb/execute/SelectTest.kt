package tech.insight.engine.innodb.execute

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.insight.core.engine.SqlPipeline
import tech.insight.engine.innodb.dropDb
import tech.insight.share.data.*


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
        SqlPipeline.executeSql(selectAll(tableName,dbName))
    }


}
