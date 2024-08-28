package tech.insight.engine.innodb.page

import tech.insight.engine.innodb.page.compact.Compact


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/


/**
 * a new [Compact] replace old [Compact],used mostly for update operation.
 *
 *
 * if new [Compact] length is greater than old [Compact] length, append the new [Compact] to free space ,
 * set old [Compact] delete flag true.
 *
 * if new [Compact] length is less than old [Compact] length, in-place replace but maybe retain dirty space.
 */
fun InnoDbPage.replace(oldCompact: Compact, newCompact: Compact) {
    val (pre, next) = findPreAndNext(oldCompact, true)
    if (oldCompact.length() >= newCompact.length()) {
        System.arraycopy(
            newCompact.toBytes(),
            0,
            userRecords.body,
            oldCompact.offsetInPage - oldCompact.beforeSplitOffset() - ConstantSize.USER_RECORDS.offset(),
            newCompact.length()
        )
        newCompact.offsetInPage =
            oldCompact.offsetInPage - oldCompact.beforeSplitOffset() + newCompact.beforeSplitOffset()
    } else {
        userRecords.addRecord(newCompact)
        newCompact.offsetInPage = this.pageHeader.heapTop.toInt() + newCompact.beforeSplitOffset()
        this.pageHeader.lastInsertOffset = (this.pageHeader.lastInsertOffset + newCompact.length()).toShort()
    }
    val oldOffset = oldCompact.offsetInPage
    val newOffset = newCompact.offsetInPage
    if (oldCompact.recordHeader.nOwned != 0) {
        this.pageDirectory.replace(oldOffset.toShort(), newOffset.toShort())
    }
    pre.recordHeader.nextRecordOffset = (newOffset - pre.absoluteOffset()).toShort()
    newCompact.recordHeader.nextRecordOffset = (next.absoluteOffset() - newOffset).toShort()
    refreshRecordHeader(pre)
    refreshRecordHeader(next)
    refreshRecordHeader(newCompact)
    if (oldCompact.length() < newCompact.length()) {
        oldCompact.recordHeader.deleteMask = true
        pageHeader.apply {
            heapTop = (heapTop + newCompact.length()).toShort()
            absoluteRecordCount = (absoluteRecordCount + 1).toShort()
            garbage = (garbage + oldCompact.length()).toShort()
            oldCompact.recordHeader.nextRecordOffset = free
            free = oldCompact.offsetInPage.toShort()
        }
        refreshRecordHeader(oldCompact)
    }
}
