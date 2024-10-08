package tech.insight.engine.innodb.execute

import java.io.File
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.environment.DefaultProperty
import tech.insight.core.environment.GlobalContext
import tech.insight.core.environment.TableManager
import tech.insight.engine.innodb.core.buffer.BufferPool
import tech.insight.engine.innodb.dropDb
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.InnodbUserRecord
import tech.insight.engine.innodb.page.Supremum
import tech.insight.engine.innodb.page.compact.RecordType
import tech.insight.engine.innodb.page.type.DataPage.Companion.FIL_PAGE_INDEX_VALUE
import tech.insight.share.data.insertBigDataCount
import tech.insight.share.data.insertData
import tech.insight.share.data.insertDataCount
import tech.insight.share.data.insertOneData
import tech.insight.share.data.selectWhereId
import tech.insight.share.data.testDb
import tech.insight.share.data.test_table
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InsertTest {


    private val dbName = "test_db"

    private val tableName = "test_table"

    @TempDir
    private var tempDir: File? = null

    @BeforeEach
    fun setHome() {
        GlobalContext[DefaultProperty.DATA_DIR] = tempDir!!.path
    }

    @BeforeEach
    fun clear() {
        dropDb(dbName)
    }

    @Test
    fun insertOneRow() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertOneData(tableName, dbName))
        val table = TableManager.require(testDb, test_table)
        val rootPage = BufferPool.getRoot(table.indexList[0] as InnodbIndex)
        val fileHeader = rootPage.fileHeader
        assertEquals(0, fileHeader.offset)
        assertEquals(0, fileHeader.next)
        assertEquals(FIL_PAGE_INDEX_VALUE, fileHeader.pageType)
        assertEquals(0, fileHeader.pre)

        val pageHeader = rootPage.pageHeader
        assertEquals(3, pageHeader.absoluteRecordCount)
        assertEquals(1, pageHeader.recordCount)
        //  120(two header + two sys record) +17(user record length)
        assertEquals(120 + 17, pageHeader.heapTop)
        //  120(two header + two sys record) +8(vars 2 + null list 1 + header 5)
        assertEquals(120 + 8, pageHeader.lastInsertOffset)
        assertEquals(0, pageHeader.level)

        rootPage.infimum.apply {
            assertEquals(0, this.recordHeader.heapNo)
            // 16 means 8 + ConstantSize.INFIMUM_BODY.size
            assertEquals(16 + ConstantSize.SUPREMUM.size, this.recordHeader.nextRecordOffset)
            assertEquals(1, this.recordHeader.nOwned)
            assertEquals(RecordType.INFIMUM, this.recordHeader.recordType)
            val userRecord = this.nextRecord()
            assertEquals(17, userRecord.length())
            assertEquals(
                rootPage.supremum.offsetInPage() - userRecord.offsetInPage(),
                userRecord.nextRecordOffset()
            )
            assertEquals(RecordType.NORMAL, userRecord.recordHeader.recordType)
            assertEquals(0, userRecord.recordHeader.nOwned)
            assertEquals(2, userRecord.recordHeader.heapNo)
            assertEquals(false, userRecord.recordHeader.deleteMask)
        }
        rootPage.supremum.apply {
            assertEquals(1, this.recordHeader.heapNo)
            assertEquals(2, this.recordHeader.nOwned)
            assertEquals(RecordType.SUPREMUM, this.recordHeader.recordType)
        }
        assertEquals(17, rootPage.userRecords.length())
    }

    @Test
    fun insertFiveRow() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertData(tableName, dbName))
        val table = TableManager.require(testDb, test_table)
        val rootPage = BufferPool.getRoot(table.indexList[0] as InnodbIndex)
        val fileHeader = rootPage.fileHeader
        assertEquals(0, fileHeader.offset)
        assertEquals(0, fileHeader.next)
        assertEquals(FIL_PAGE_INDEX_VALUE, fileHeader.pageType)
        assertEquals(0, fileHeader.pre)

        val pageHeader = rootPage.pageHeader
        assertEquals(7, pageHeader.absoluteRecordCount)
        assertEquals(5, pageHeader.recordCount)
        //  120(two header + two sys record) +16(user record length)
        assertEquals((120 + 16 * 5), pageHeader.heapTop)
        //  120(two header + two sys record) +8(vars 2 + null list 1 + header 5)
        assertEquals((120 + 16 * 4 + 8), pageHeader.lastInsertOffset)
        assertEquals(0, pageHeader.level)

        val infimum = rootPage.infimum
        infimum.apply {
            assertEquals(0, this.recordHeader.heapNo)
            // 16 means 8 + ConstantSize.INFIMUM_BODY.size
            assertEquals(16 + ConstantSize.SUPREMUM.size, nextRecordOffset())
            assertEquals(1, this.recordHeader.nOwned)
            assertEquals(RecordType.INFIMUM, this.recordHeader.recordType)
        }
        rootPage.supremum.apply {
            assertEquals(1, this.recordHeader.heapNo)
            assertEquals(6, this.recordHeader.nOwned)
            assertEquals(RecordType.SUPREMUM, this.recordHeader.recordType)
        }
        assertEquals(16 * 5, rootPage.userRecords.length())

        var pre: InnodbUserRecord = infimum
        for (i in 0 until 4) {
            val userRecord = pre.nextRecord()
            assertEquals(16, userRecord.length())
            assertEquals(RecordType.NORMAL, userRecord.recordHeader.recordType)
            assertEquals(0, userRecord.recordHeader.nOwned)
            assertEquals(2 + i, userRecord.recordHeader.heapNo)
            assertEquals(false, userRecord.recordHeader.deleteMask)
            pre = userRecord
        }
        val lastUserRecord = rootPage.getUserRecordByOffset(pre.nextRecordOffset() + pre.offsetInPage())
        assertEquals(16, lastUserRecord.length())
        assertEquals(RecordType.NORMAL, lastUserRecord.recordHeader.recordType)
        assertEquals(0, lastUserRecord.recordHeader.nOwned)
        assertEquals(6, lastUserRecord.recordHeader.heapNo)
        assertEquals(false, lastUserRecord.recordHeader.deleteMask)
        assertEquals(
            rootPage.supremum.offsetInPage(),
            lastUserRecord.nextRecordOffset() + lastUserRecord.offsetInPage()
        )
    }


    /**
     * This test case will trigger exactly dictionary split
     */
    @Test
    fun insertPageDictionarySplitPage() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertDataCount(tableName, dbName, 10))
        val table = TableManager.require(testDb, test_table)
        val rootPage = BufferPool.getRoot(table.indexList[0] as InnodbIndex)
        val pageHeader = rootPage.pageHeader
        assertEquals(12, pageHeader.absoluteRecordCount)
        assertEquals(10, pageHeader.recordCount)
        assertEquals(3, pageHeader.slotCount)
        //  120(two header + two sys record) +17(user record length)
        assertEquals((120 + 17 * 9 + 18), pageHeader.heapTop)
        //  120(two header + two sys record) +8(vars 2 + null list 1 + header 5)
        assertEquals((120 + 17 * 9 + 8), pageHeader.lastInsertOffset)
        assertEquals(0, pageHeader.level)

        val infimum = rootPage.infimum
        infimum.apply {
            assertEquals(0, this.recordHeader.heapNo)
            // 16 means 8 + ConstantSize.INFIMUM_BODY.size
            assertEquals(16 + ConstantSize.SUPREMUM.size, nextRecordOffset())
            assertEquals(1, this.recordHeader.nOwned)
            assertEquals(RecordType.INFIMUM, this.recordHeader.recordType)
        }
        rootPage.supremum.apply {
            assertEquals(1, this.recordHeader.heapNo)
            assertEquals(7, this.recordHeader.nOwned)
            assertEquals(RecordType.SUPREMUM, this.recordHeader.recordType)
        }
        assertEquals(17 * 9 + 18, rootPage.userRecords.length())

        var pre: InnodbUserRecord = infimum
        for (i in 0 until 9) {
            val userRecord = rootPage.getUserRecordByOffset(pre.nextRecordOffset() + pre.offsetInPage())
            assertEquals(17, userRecord.length())
            assertEquals(RecordType.NORMAL, userRecord.recordHeader.recordType)
            assertEquals(if (i == 3) 4 else 0, userRecord.recordHeader.nOwned)
            assertEquals(2 + i, userRecord.recordHeader.heapNo)
            assertEquals(false, userRecord.recordHeader.deleteMask)
            pre = userRecord
        }
        val lastUserRecord = rootPage.getUserRecordByOffset(pre.nextRecordOffset() + pre.offsetInPage())
        assertEquals(18, lastUserRecord.length())
        assertEquals(RecordType.NORMAL, lastUserRecord.recordHeader.recordType)
        assertEquals(0, lastUserRecord.recordHeader.nOwned)
        assertEquals(11, lastUserRecord.recordHeader.heapNo)
        assertEquals(false, lastUserRecord.recordHeader.deleteMask)
        assertEquals(
            rootPage.supremum.offsetInPage(),
            lastUserRecord.nextRecordOffset() + lastUserRecord.offsetInPage()
        )
    }

    @Test
    fun insertPageTwiceDictionarySplitPage() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertDataCount(tableName, dbName, 15))
        val table = TableManager.require(testDb, test_table)
        val rootPage = BufferPool.getRoot(table.indexList[0] as InnodbIndex)
        val pageHeader = rootPage.pageHeader
        assertEquals(17, pageHeader.absoluteRecordCount)
        assertEquals(15, pageHeader.recordCount)
        //  120(two header + two sys record) +17(user record length)
        assertEquals((120 + 17 * 9 + 18 * 6), pageHeader.heapTop)
        //  120(two header + two sys record) +8(vars 2 + null list 1 + header 5)
        assertEquals((120 + 17 * 9 + 18 * 5 + 8), pageHeader.lastInsertOffset)
        assertEquals(0, pageHeader.level)
        assertEquals(4, pageHeader.slotCount)
        rootPage.supremum.apply {
            assertEquals(1, this.recordHeader.heapNo)
            assertEquals(8, this.recordHeader.nOwned)
            assertEquals(RecordType.SUPREMUM, this.recordHeader.recordType)
        }
        assertEquals(17 * 9 + 18 * 6, rootPage.userRecords.length())

        var pre: InnodbUserRecord = rootPage.infimum
        //  first group 1-4  count:4
        for (i in 0 until 4) {
            val userRecord = pre.nextRecord()
            assertEquals(17, userRecord.length())
            assertEquals(RecordType.NORMAL, userRecord.recordHeader.recordType)
            assertEquals(if (i == 3) 4 else 0, userRecord.recordHeader.nOwned)
            assertEquals(2 + i, userRecord.recordHeader.heapNo)
            assertEquals(false, userRecord.recordHeader.deleteMask)
            pre = userRecord
        }

        //  second group 5-8 count:4
        for (i in 0 until 4) {
            val userRecord = pre.nextRecord()
            assertEquals(17, userRecord.length())
            assertEquals(RecordType.NORMAL, userRecord.recordHeader.recordType)
            assertEquals(if (i == 3) 4 else 0, userRecord.recordHeader.nOwned)
            assertEquals(6 + i, userRecord.recordHeader.heapNo)
            assertEquals(false, userRecord.recordHeader.deleteMask)
            pre = userRecord
        }

        //  third group 9-15 count 8(include supremum)
        for (i in 0 until 7) {
            val userRecord = pre.nextRecord()
            assertEquals(if (i == 0) 17 else 18, userRecord.length())
            assertEquals(RecordType.NORMAL, userRecord.recordHeader.recordType)
            assertEquals(0, userRecord.recordHeader.nOwned)
            assertEquals(10 + i, userRecord.recordHeader.heapNo)
            assertEquals(false, userRecord.recordHeader.deleteMask)
            pre = userRecord
        }

        val lastUserRecord = pre.nextRecord()
        assert(lastUserRecord is Supremum)
        assertEquals(13, lastUserRecord.length())
        assertEquals(RecordType.SUPREMUM, lastUserRecord.recordHeader.recordType)
        assertEquals(8, lastUserRecord.recordHeader.nOwned)
        assertEquals(1, lastUserRecord.recordHeader.heapNo)
        assertEquals(false, lastUserRecord.recordHeader.deleteMask)
    }


    @Test
    fun largeInsertTest() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertDataCount(tableName, dbName, 1000))
        assertNotNull(TableManager.require(testDb, test_table))
        for (i in 0..10) {
            val select = SqlPipeline.executeSql(selectWhereId(i * 100, tableName, dbName))
            println(select)
        }
    }

    @Test
    fun vastInsertTest() {
        CreateTableTest().correctTest()
        SqlPipeline.executeSql(insertBigDataCount(tableName, dbName, 3000))
        assertNotNull(TableManager.require(testDb, test_table))
        for (i in 0..30) {
            val select = SqlPipeline.executeSql(selectWhereId(i, tableName, dbName))
            println(select)
        }

    }


}

