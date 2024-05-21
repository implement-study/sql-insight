package tech.insight.engine.innodb.execute

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.environment.TableManager
import tech.insight.engine.innodb.dropDb
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.Supremum
import tech.insight.engine.innodb.page.compact.RecordType
import tech.insight.engine.innodb.page.type.DataPage.Companion.FIL_PAGE_INDEX_VALUE
import tech.insight.engine.innodb.utils.PageSupport
import tech.insight.share.data.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InsertTest {


    private val dbName = "test_db"

    private val tableName = "test_table"

    @BeforeEach
    @AfterEach
    fun clear() {
        dropDb(dbName)
    }

    @Test
    fun insertOneRow() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertOneData(tableName, dbName))
        val table = TableManager.require(testDb, test_table)
        val rootPage = PageSupport.getRoot(table.indexList[0] as InnodbIndex)
        val fileHeader = rootPage.fileHeader
        assertEquals(0, fileHeader.offset)
        assertEquals(0, fileHeader.next)
        assertEquals(FIL_PAGE_INDEX_VALUE, fileHeader.pageType)
        assertEquals(0, fileHeader.pre)

        val pageHeader = rootPage.pageHeader
        assertEquals(3, pageHeader.absoluteRecordCount)
        assertEquals(1, pageHeader.recordCount)
        //  120(two header + two sys record) +16(user record length)
        assertEquals((120 + 16).toShort(), pageHeader.heapTop)
        //  120(two header + two sys record) +8(vars 2 + null list 1 + header 5)
        assertEquals((120 + 8).toShort(), pageHeader.lastInsertOffset)
        assertEquals(0, pageHeader.level)

        rootPage.infimum.apply {
            assertEquals(0U, this.recordHeader.heapNo)
            assertEquals(16+ConstantSize.SUPREMUM.size(), this.recordHeader.nextRecordOffset)
            assertEquals(1, this.recordHeader.nOwned)
            assertEquals(RecordType.INFIMUM, this.recordHeader.recordType)
        }
        rootPage.supremum.apply {
            assertEquals(1U, this.recordHeader.heapNo)
            assertEquals(0, this.recordHeader.nextRecordOffset)
            assertEquals(6, this.recordHeader.nOwned)
            assertEquals(RecordType.SUPREMUM, this.recordHeader.recordType)
        }
        assertEquals(16 * 5, rootPage.userRecords.length())
    }

    @Test
    fun insertFiveRow() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertData(tableName, dbName))
    }


    /**
     * This test case will trigger exactly dictionary split
     */
    @Test
    fun insertPageDictionarySplitPage() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertDataCount(tableName, dbName, 10))
        assertDoesNotThrow { TableManager.require(testDb, test_table) }
    }


    @Test
    fun largeInsertTest() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertDataCount(tableName, dbName, 1000))
        assertNotNull(TableManager.require(testDb, test_table))
    }
    //
    //    @Test
    //    fun twoTimeInsertTest() {
    //        ExecutePlanTest().createTableTest()
    //        SqlPipeline.executeSql(largeInsert)
    //        SqlPipeline.executeSql(largeInsert)
    //        val table = TableManager.require(testDb, test_table)
    //        val jsonFile = JsonEngineSupport.getJsonFile(table)
    //        jsonFile.useLines {
    //            assertEquals(2000, it.filter { line -> line.isNotEmpty() }.count())
    //        }
    //    }

}

