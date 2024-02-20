/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/sql-insight/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.insight.engine.innodb.page

import org.gongxuanzhang.easybyte.core.ByteWrapper
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.engine.innodb.index.InnodbIndex


/**
 * InnoDb Page
 * size default 16K.
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 */
abstract class InnoDbPage protected constructor(index: InnodbIndex) : ByteWrapper, Comparator<InnodbUserRecord?>,
    PageObject, Iterable<InnodbUserRecord?> {
    /**
     * 38 bytes
     */
    var fileHeader: FileHeader? = null

    /**
     * 56 bytes
     */
    var pageHeader: PageHeader? = null

    /**
     * 13 bytes
     */
    var infimum: Infimum? = null

    /**
     * 13 bytes
     */
    var supremum: Supremum? = null

    /**
     * uncertain bytes.
     * user records bytes + freeSpace = page size - other fixed size
     */
    var userRecords: UserRecords? = null

    /**
     * uncertain bytes.
     */
    var pageDirectory: PageDirectory? = null

    /**
     * 8 bytes
     */
    var fileTrailer: FileTrailer? = null
    var ext: PageExt

    init {
        ext = PageExt()
        ext.belongIndex = index
    }

   override fun toBytes(): ByteArray {
        val buffer: DynamicByteBuffer = DynamicByteBuffer.allocate()
        buffer.append(fileHeader!!.toBytes())
        buffer.append(pageHeader!!.toBytes())
        buffer.append(infimum.toBytes())
        buffer.append(supremum.toBytes())
        buffer.append(userRecords!!.toBytes())
        buffer.append(ByteArray(freeSpace.toInt()))
        buffer.append(pageDirectory!!.toBytes())
        buffer.append(fileTrailer!!.toBytes())
        return buffer.toBytes()
    }

    fun pageType(): PageType {
        return PageType.Companion.valueOf(fileHeader!!.pageType.toInt())
    }

    val freeSpace: Short
        get() = (ConstantSize.PAGE.size() -
                ConstantSize.PAGE_HEADER.size() -
                ConstantSize.FILE_HEADER.size() -
                ConstantSize.FILE_TRAILER.size() -
                ConstantSize.SUPREMUM.size() -
                ConstantSize.INFIMUM.size() -
                pageDirectory!!.length() - userRecords!!.length()).toShort()

    /**
     * add a data.
     * data means a insert row .
     * page is leaf node will insert data.
     * page is index node will find target leaf node and insert data.
     * may be split page in process
     */
    open fun insertData(data: InnodbUserRecord) {
        val (pre, next) = findPreAndNext(data)
        linkedAndAdjust(pre, data, next)
        splitIfNecessary()
        PageSupport.flushPage(this)
    }

    /**
     * find location where the record linked in page and return pre and next.
     * search only,don't affect the page .
     *
     * @param userRecord target record
     */
    protected fun findPreAndNext(userRecord: InnodbUserRecord?): Pair<InnodbUserRecord?, InnodbUserRecord?> {
        val targetSlot = findTargetSlot(userRecord)
        var pre = getUserRecordByOffset(pageDirectory!!.indexSlot(targetSlot + 1).toInt())
        var next = getUserRecordByOffset(pre.nextRecordOffset() + pre.offset())
        while (compare(userRecord, next) > 0) {
            pre = next
            next = getUserRecordByOffset(pre.nextRecordOffset() + pre.offset())
        }
        return Pair(pre, next)
    }

    /**
     * find the slot where the target record is located
     *
     *
     * return 0 means supremum
     *
     * @return slot index is user record inserted never return `slot.length -1 ` because slot.length - 1 is the
     * infimum
     */
    protected fun findTargetSlot(userRecord: InnodbUserRecord?): Int {
        var left = 0
        var right = pageDirectory!!.slotCount() - 1
        while (left < right - 1) {
            val mid = (right + left) / 2
            val offset: Short = pageDirectory.getSlots().get(mid)
            val base = getUserRecordByOffset(offset.toInt())
            val compare = compare(userRecord, base)
            if (compare == 0) {
                throw DuplicationPrimaryKeyException(base.getRowId())
            }
            if (compare < 0) {
                left = mid
            } else {
                right = mid
            }
        }
        val base = getUserRecordByOffset(pageDirectory.getSlots().get(right))
        val compare = compare(userRecord, base)
        if (compare == 0) {
            throw DuplicationPrimaryKeyException(base.getRowId())
        }
        return if (compare > 0) {
            left
        } else right
    }

    /**
     * @param offsetInPage offset in page
     * @return user record
     */
    protected fun getUserRecordByOffset(offsetInPage: Int): InnodbUserRecord? {
        if (offsetInPage == ConstantSize.INFIMUM.offset()) {
            return infimum
        }
        if (offsetInPage == ConstantSize.SUPREMUM.offset()) {
            return supremum
        }
        val wrap = wrapUserRecord(offsetInPage)
        wrap.setOffset(offsetInPage)
        return wrap
    }

    protected abstract fun wrapUserRecord(offsetInPage: Int): InnodbUserRecord

    /**
     * this page should split.
     * in general after insert row call this method
     */
    protected abstract fun splitIfNecessary()
    protected fun upgrade(prePage: InnoDbPage, nextPage: InnoDbPage) {
        //  is root page
        if (ext.parent == null) {
            rootPageUpgrade(prePage, nextPage)
        } else {
            // normal leaf node
            prePage.fileHeader.setOffset(fileHeader!!.offset)
            val newDataPageOffset: Int = PageSupport.allocatePage(ext.belongIndex)
            nextPage.fileHeader.setOffset(newDataPageOffset)
            val parent: InnoDbPage? = ext.parent
            transferFrom(prePage)
            parent!!.insertData(nextPage.pageIndex())
        }
    }

    /**
     * get the first index node to parent node insert
     */
    abstract fun pageIndex(): IndexRecord

    /**
     * root page upgrade.
     * create 2 new page and linked whether upgrading  from index page or data page.
     */
    private fun rootPageUpgrade(preChild: InnoDbPage, secondChild: InnoDbPage) {
        preChild.pageHeader!!.level = pageHeader!!.level
        secondChild.pageHeader!!.level = pageHeader!!.level
        pageHeader!!.level++
        val firstFileHeader: FileHeader = preChild.getFileHeader()
        val secondFileHeader: FileHeader = secondChild.getFileHeader()
        val offset: Int = PageSupport.allocatePage(ext.belongIndex, 2)
        firstFileHeader.setOffset(offset)
        secondFileHeader.setOffset(offset + ConstantSize.PAGE.size())
        firstFileHeader.setPre(-1)
        firstFileHeader.setNext(secondFileHeader.offset)
        secondFileHeader.setPre(firstFileHeader.offset)
        secondFileHeader.setNext(-1)
        //  transfer to index page
        fileHeader!!.next = -1
        fileHeader!!.pageType = PageType.FIL_PAGE_INODE.value
        pageHeader = PageHeaderFactory.createPageHeader()
        pageDirectory = PageDirectory()
        //  clear user record
        userRecords = UserRecords()
        insertData(preChild.pageIndex())
        insertData(secondChild.pageIndex())
    }

    protected fun linkedAndAdjust(pre: InnodbUserRecord?, insertRecord: InnodbUserRecord, next: InnodbUserRecord?) {
        var next = next
        val insertHeader: RecordHeader = insertRecord.getRecordHeader()
        insertHeader.setHeapNo(pageHeader!!.absoluteRecordCount.toInt())
        insertRecord.setOffset(pageHeader!!.lastInsertOffset + insertRecord.beforeSplitOffset())
        insertHeader.setNextRecordOffset(next.offset() - insertRecord.offset())
        pre.getRecordHeader().setNextRecordOffset(insertRecord.offset() - pre.offset())
        refreshRecordHeader(pre)
        insertHeader.setRecordType(RecordType.NORMAL)

        //  adjust page
        userRecords!!.addRecord(insertRecord)
        pageHeader!!.absoluteRecordCount++
        pageHeader!!.recordCount++
        pageHeader!!.heapTop = (pageHeader!!.heapTop + insertRecord.length().toShort()).toShort()
        pageHeader!!.lastInsertOffset = (pageHeader!!.lastInsertOffset + insertRecord.length().toShort()).toShort()


        //  adjust group
        while (next.getRecordHeader().getNOwned() === 0) {
            next = getUserRecordByOffset(next.offset() + next.nextRecordOffset())
        }
        val nextHeader: RecordHeader = next.getRecordHeader()
        val groupCount: Int = nextHeader.getNOwned()
        nextHeader.setNOwned(groupCount + 1)
        if (next.getRecordHeader().getNOwned() <= Constant.Companion.SLOT_MAX_COUNT) {
            return
        }
        log.info("start group split ...")
        for (i in 0 until pageDirectory!!.slots.size - 1) {
            if (pageDirectory!!.slots[i] == next.offset()) {
                var preGroupMax = getUserRecordByOffset(pageDirectory!!.slots[i + 1].toInt())
                for (j in 0 until (Constant.Companion.SLOT_MAX_COUNT shr 1)) {
                    preGroupMax = getUserRecordByOffset(preGroupMax.offset() + preGroupMax.nextRecordOffset())
                }
                pageDirectory!!.split(i, preGroupMax.offset() as Short)
            }
        }
        log.info("end group split ...")
    }

    /**
     * byte array copy from target page
     */
    fun transferFrom(page: InnoDbPage) {
        val snapshot: InnoDbPage = PageFactory.swap(page.toBytes(), ext.belongIndex)
        fileHeader = snapshot.fileHeader
        pageHeader = snapshot.pageHeader
        infimum = snapshot.infimum
        supremum = snapshot.supremum
        userRecords = snapshot.userRecords
        pageDirectory = snapshot.pageDirectory
        fileTrailer = snapshot.fileTrailer
        ext = snapshot.ext
    }

    private fun refreshRecordHeader(record: InnodbUserRecord?) {
        if (record is Infimum) {
            return
        }
        if (record is Supremum) {
            return
        }
        val headerByte = record.getRecordHeader().toBytes()
        val bodyOffset: Int = record.offset() - ConstantSize.RECORD_HEADER.size() - ConstantSize.PAGE_HEADER.size() -
                ConstantSize.FILE_HEADER.size() -
                ConstantSize.SUPREMUM.size() -
                ConstantSize.INFIMUM.size()
        System.arraycopy(headerByte, 0, userRecords!!.body, bodyOffset, headerByte.size)
    }

    override fun length(): Int {
        return ConstantSize.PAGE.size()
    }

    override fun iterator(): MutableIterator<InnodbUserRecord> {
        return Itr()
    }

    inner class Itr : MutableIterator<InnodbUserRecord> {
        private var cursor = getUserRecordByOffset(infimum!!.nextRecordOffset() + infimum!!.offset())
        override fun hasNext(): Boolean {
            return cursor !== supremum
        }

        override fun next(): InnodbUserRecord {
            if (cursor === supremum) {
                throw NoSuchElementException()
            }
            val result = cursor
            cursor = getUserRecordByOffset(cursor.nextRecordOffset() + cursor.offset())
            return result
        }
    }

    class PageExt {
        lateinit var belongIndex: InnodbIndex
        var parent: IndexPage? = null
    }
}
