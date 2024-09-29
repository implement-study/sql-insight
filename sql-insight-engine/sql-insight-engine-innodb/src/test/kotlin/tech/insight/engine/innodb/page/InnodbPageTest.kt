package tech.insight.engine.innodb.page

import io.netty.buffer.Unpooled.wrappedBuffer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import tech.insight.buffer.wrappedBuf
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.ValueInt
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.environment.EngineManager
import tech.insight.core.environment.TableManager
import tech.insight.engine.innodb.core.buffer.BufferPool
import tech.insight.engine.innodb.dropDb
import tech.insight.engine.innodb.execute.CreateTableTest
import tech.insight.engine.innodb.index.InnodbIndex
import tech.insight.engine.innodb.page.compact.RecordHeader
import tech.insight.share.data.insertDataCount
import kotlin.test.assertEquals


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InnodbPageTest {

    private val dbName = "test_db"

    private val tableName = "test_table"

    private lateinit var table: Table

    @BeforeEach
    fun preparePage() {
        dropDb(dbName)
        CreateTableTest().correctTest()
        table = TableManager.require(dbName, tableName)
        EngineManager.selectEngine("innoDB").openTable(table)
    }

    @AfterEach
    fun clear() {
        dropDb(dbName)
    }

    @Test
    fun innodbPageFindSlot() {
        SqlPipeline.executeSql(insertDataCount(tableName, dbName, 500))
        val innodbPage = BufferPool.getRoot(table.indexList[0] as InnodbIndex)

        for (i in 0 until 500) {
            val record = mockUserRecord(i, table)
            val targetSlot = innodbPage.pageDirectory.findTargetIn(record)
            val big = targetSlot.maxRecord()
            val small = targetSlot.smaller().maxRecord()
            assert(big >= record)
            assert(small < record)
        }

    }


    @Test
    fun testFillRecordsToPageDirectorySlotCount() {
        var innodbPage = BufferPool.getRoot(table.indexList[0] as InnodbIndex)
        val pageBytes = innodbPage.toBytes()
        val recordList = mutableListOf<InnodbUserRecord>()
        for (id in 1..5) {
            recordList.add(mockUserRecord(id, table))
        }
        innodbPage.coverRecords(recordList)
        assertEquals(2, innodbPage.pageDirectory.slots.size)
        assertEquals(innodbPage.supremum.offsetInPage(), innodbPage.pageDirectory.slots[0])
        assertEquals(innodbPage.infimum.offsetInPage(), innodbPage.pageDirectory.slots[1])
        val supremumOffset = innodbPage.supremum.offsetInPage()

        for (id in 6..15) {
            recordList.add(mockUserRecord(id, table))
        }
        innodbPage = InnoDbPage(wrappedBuffer(pageBytes), table.indexList[0] as InnodbIndex)
        innodbPage.coverRecords(recordList)
        assertEquals(3, innodbPage.pageDirectory.slots.size)
        assertEquals(innodbPage.supremum.offsetInPage(), innodbPage.pageDirectory.slots[0])
        assertEquals(innodbPage.infimum.offsetInPage(), innodbPage.pageDirectory.slots[2])

        recordList.add(mockUserRecord(16, table))
        innodbPage = InnoDbPage(wrappedBuf(pageBytes), table.indexList[0] as InnodbIndex)

        innodbPage.coverRecords(recordList)
        assertEquals(4, innodbPage.pageDirectory.slots.size)
        assertEquals(supremumOffset, innodbPage.pageDirectory.slots[0])
        assertEquals(innodbPage.infimum.offsetInPage(), innodbPage.pageDirectory.slots[3])
    }

    private fun mockUserRecord(id: Int, table: Table): InnodbUserRecord {
        return mock<InnodbUserRecord> {
            on { indexKey() } doReturn arrayOf(ValueInt(id))
            on { belongIndex() } doReturn table.indexList[0] as InnodbIndex
            on { belongTo() } doReturn table
            on { getValueByColumnName("id") } doReturn ValueInt(id)
            on { toBytes() } doReturn ByteArray(16)
            on { length() } doReturn 16 + id.toString().length
            on { beforeSplitOffset() } doReturn 8
            on { recordHeader } doReturn mock<RecordHeader>()
        }
    }


}
