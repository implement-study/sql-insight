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
import tech.insight.buffer.getAllBytes
import tech.insight.engine.innodb.page.PageHeader.Companion.EMPTY_PAGE_HEAP_TOP

/**
 *
 * source is belong innodb page snapshot [ByteBuf],
 * contains all userRecords (include delete mask record) and free space
 * @author gongxuanzhangmelt@gmail.com
 */
class UserRecords(override val parentPage: InnoDbPage) : PageObject {

    val source: ByteBuf = parentPage.source

    override fun toBytes(): ByteArray {
        val length = parentPage.pageHeader.heapTop - ConstantSize.USER_RECORDS.offset
        source.slice(ConstantSize.USER_RECORDS.offset, length).let {
            val bytes = it.getAllBytes()
            return bytes
        }
    }

    /**
     * this method will adjust inner data from page.
     * invoker should ensure the this page can add record.
     *
     * param user record not direct reference innodb page source
     * after add use return record
     * @return record in page,
     */
    fun addRecord(userRecord: InnodbUserRecord): InnodbUserRecord {
        check(parentPage.remainSpace() >= userRecord.length() + Short.SIZE_BYTES) {
            "this page dont have more space "
        }
        userRecord.recordHeader.heapNo = parentPage.pageHeader.absoluteRecordCount
        this.source.setBytes(parentPage.pageHeader.heapTop, userRecord.toBytes())
        this.parentPage.apply {
            val recordInPage = pageHeader.heapTop + userRecord.beforeSplitOffset()
            userRecord.setOffsetInPage(recordInPage)
            pageHeader.addRecord(userRecord)
            return getUserRecordByOffset(recordInPage)
        }
    }

    fun addRecords(userRecords: List<InnodbUserRecord>) {
        check(parentPage.remainSpace() >= userRecords.sumOf { it.length() } + Short.SIZE_BITS * userRecords.size / 4) {
            "this page dont have more space "
        }
        //  todo batch insert
        userRecords.forEach { record ->
            this.source.writeBytes(record.toBytes())
            this.parentPage.pageHeader.addRecord(record)
        }
    }

    fun coverRecord(oldRecord: InnodbUserRecord, newRecord: InnodbUserRecord) {
        check(oldRecord.length() >= newRecord.length()) {
            "new record length must less than old record"
        }
        if (newRecord.beforeSplitOffset() != oldRecord.beforeSplitOffset()) {
            val preRecord = oldRecord.preRecord()
            val nextRecord = oldRecord.nextRecord()
            preRecord.linkRecord(newRecord)
            nextRecord.linkRecord(nextRecord)
            parentPage.pageDirectory.replace(oldRecord.offsetInPage(), newRecord.offsetInPage())
        }
        this.parentPage.source.setBytes(oldRecord.offsetInPage() - oldRecord.beforeSplitOffset(), newRecord.toBytes())
    }


    fun clear() {
        parentPage.infimum.recordHeader.nextRecordOffset = Supremum.OFFSET_IN_PAGE - Infimum.OFFSET_IN_PAGE
        parentPage.supremum.apply {
            recordHeader.nextRecordOffset = 0
            recordHeader.nOwned = 1
        }
        parentPage.pageDirectory.clear()
        parentPage.pageHeader.apply {
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
        return parentPage.pageHeader.heapTop - ConstantSize.USER_RECORDS.offset
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
        return "UserRecords(length=${this.length()})"
    }

}
