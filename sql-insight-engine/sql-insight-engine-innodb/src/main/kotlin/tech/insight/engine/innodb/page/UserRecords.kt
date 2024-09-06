/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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

import io.netty.buffer.ByteBuf
import tech.insight.buffer.readAllBytes
import tech.insight.engine.innodb.page.PageHeader.Companion.EMPTY_PAGE_HEAP_TOP

/**
 *
 * source is belong innodb page snapshot [ByteBuf],
 * contains all userRecords (include delete mask record) and free space
 * @author gongxuanzhangmelt@gmail.com
 */
class UserRecords(override val belongPage: InnoDbPage) : PageObject {

    val source: ByteBuf

    init {
        val pageDirOffset = ConstantSize.FILE_TRAILER.offset - Short.SIZE_BYTES * belongPage.pageHeader.slotCount
        source =
            belongPage.source.slice(ConstantSize.USER_RECORDS.offset, pageDirOffset - ConstantSize.USER_RECORDS.offset)
        source.writerIndex(belongPage.pageHeader.heapTop)
    }

    override fun toBytes(): ByteArray {
        return source.readAllBytes()
    }

    /**
     * this method will adjust inner data from page.
     * invoker should ensure the this page can add record
     */
    fun addRecord(userRecord: InnodbUserRecord, spread: Boolean = true) {
        check(belongPage.remainSpace() >= userRecord.length() + Short.SIZE_BYTES) {
            "this page dont have more space "
        }
        this.source.writeBytes(userRecord.toBytes())
        if (spread) {
            this.belongPage.pageHeader.addRecord(userRecord)
        }
    }

    fun addRecords(userRecords: List<InnodbUserRecord>) {
        check(belongPage.remainSpace() >= userRecords.sumOf { it.length() } + Short.SIZE_BITS * userRecords.size / 4) {
            "this page dont have more space "
        }
        userRecords.forEach { record ->
            this.source.writeBytes(record.toBytes())
            this.belongPage.pageHeader.addRecord(record)
        }
    }

    fun coverRecord(oldRecord: InnodbUserRecord, newRecord: InnodbUserRecord) {
        check(oldRecord.length() >= newRecord.length()) {
            "new record length must less than old record"
        }
        if (newRecord.beforeSplitOffset() != oldRecord.beforeSplitOffset()) {
            val preRecord = oldRecord.preRecord()
            val nextRecord = oldRecord.nextRecord()
            preRecord.recordHeader.nextRecordOffset = newRecord.absoluteOffset() - preRecord.absoluteOffset()
            newRecord.recordHeader.nextRecordOffset = nextRecord.absoluteOffset() - newRecord.absoluteOffset()
            belongPage.pageDirectory.replace(oldRecord.absoluteOffset(), newRecord.absoluteOffset())
        }
        this.belongPage.source.setBytes(oldRecord.absoluteOffset(), newRecord.toBytes())
    }


    fun clear() {
        belongPage.infimum.recordHeader.nextRecordOffset = Supremum.OFFSET_IN_PAGE - Infimum.OFFSET_IN_PAGE
        belongPage.supremum.apply {
            recordHeader.nextRecordOffset = 0
            recordHeader.nOwned = 1
        }
        belongPage.pageDirectory.clear()
        belongPage.pageHeader.apply {
            slotCount = 2
            heapTop = EMPTY_PAGE_HEAP_TOP
            absoluteRecordCount = 2
            recordCount = 0
            deleteStart = 0
            garbage = 0
            lastInsertOffset = 0
            direction = 0
            directionCount = 0
        }
    }

    override fun length(): Int {
        return this.source.capacity()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserRecords) return false
        return this.source == other.source
    }

    override fun hashCode(): Int {
        return source.hashCode()
    }

    override fun toString(): String {
        //  todo
        return "UserRecords(length=${source.readableBytes()})"
    }

}
