package tech.insight.engine.innodb.factory

import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.PageHeader
import java.nio.ByteBuffer

/**
 * @author gongxuanzhangmelt@gmail.com
 */
object PageHeaderFactory {
    val EMPTY_PAGE_HEAP_TOP = (ConstantSize.FILE_HEADER.size() +
            ConstantSize.PAGE_HEADER.size() +
            ConstantSize.INFIMUM.size() +
            ConstantSize.SUPREMUM.size()).toShort()

    /**
     * create a empty page header
     */
    fun createPageHeader(): PageHeader {
        val pageHeader = PageHeader()
        pageHeader.slotCount = 2.toShort()
        pageHeader.heapTop = EMPTY_PAGE_HEAP_TOP
        pageHeader.absoluteRecordCount = 2.toShort()
        pageHeader.recordCount = 0.toShort()
        pageHeader.free = 0.toShort()
        pageHeader.garbage = 0.toShort()
        pageHeader.lastInsertOffset = EMPTY_PAGE_HEAP_TOP
        pageHeader.level = 0.toShort()
        pageHeader.direction = 0.toShort()
        pageHeader.directionCount = 0.toShort()
        pageHeader.maxTransactionId = 0L
        pageHeader.indexId = 0
        pageHeader.segLeafPre = 0.toShort()
        pageHeader.segLeaf = 0L
        pageHeader.segTopPre = 0.toShort()
        pageHeader.segTop = 0L
        return pageHeader
    }

    fun readPageHeader(pageHeaderArr: ByteArray?): PageHeader {
        ConstantSize.PAGE_HEADER.checkSize(pageHeaderArr!!)
        val buffer = ByteBuffer.wrap(pageHeaderArr)
        val pageHeader = PageHeader()
        pageHeader.slotCount = buffer.getShort()
        pageHeader.heapTop = buffer.getShort()
        pageHeader.absoluteRecordCount = buffer.getShort()
        pageHeader.recordCount = buffer.getShort()
        pageHeader.free = buffer.getShort()
        pageHeader.garbage = buffer.getShort()
        pageHeader.lastInsertOffset = buffer.getShort()
        pageHeader.direction = buffer.getShort()
        pageHeader.directionCount = buffer.getShort()
        pageHeader.maxTransactionId = buffer.getLong()
        pageHeader.level = buffer.getShort()
        pageHeader.indexId = buffer.getLong()
        pageHeader.segLeafPre = buffer.getShort()
        pageHeader.segLeaf = buffer.getLong()
        pageHeader.segTopPre = buffer.getShort()
        pageHeader.segTop = buffer.getLong()
        return pageHeader
    }
}
