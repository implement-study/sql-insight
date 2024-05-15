package tech.insight.engine.innodb.page

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import tech.insight.core.bean.value.ValueInt
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.environment.TableManager
import tech.insight.engine.innodb.dropDb
import tech.insight.engine.innodb.execute.CreateTableTest
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.utils.PageSupport
import tech.insight.share.data.insertDataCount


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InnodbPageTest {

    private val dbName = "test_db"

    private val tableName = "test_table"

    private lateinit var innodbPage: InnoDbPage

    @BeforeEach
    fun preparePage() {
        dropDb(dbName)
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertDataCount(tableName, dbName, 500))
        val table = TableManager.require(dbName, tableName)
        innodbPage = PageSupport.getRoot(table.indexList[0] as InnodbIndex)
    }

    @AfterEach
    fun clear() {
        dropDb(dbName)
    }

    @Test
    fun innodbPageFindSlot() {
        val record = mock<InnodbUserRecord> {
            on { indexKey() } doReturn arrayOf(ValueInt(40))
        }
        val key = record.indexKey()
        innodbPage.findTargetSlot(record)
    }

}
