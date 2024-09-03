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
    if (oldCompact.length() >= newCompact.length()) {
        return userRecords.coverRecord(oldCompact, newCompact)
    }
    newCompact.offsetInPage = this.pageHeader.heapTop + newCompact.beforeSplitOffset()
    val pre = oldCompact.preRecord()
    val next = oldCompact.nextRecord()
    this.pageDirectory.replace(oldCompact.offsetInPage, newCompact.offsetInPage)
    userRecords.addRecord(newCompact,false)
    pre.recordHeader.nextRecordOffset = newCompact.offsetInPage - pre.absoluteOffset()
    newCompact.recordHeader.nextRecordOffset = next.absoluteOffset() - newCompact.absoluteOffset()
    oldCompact.recordHeader.deleteMask = true
    pageHeader.apply {
        heapTop += newCompact.length()
        absoluteRecordCount += 1
        garbage += oldCompact.length()
        oldCompact.recordHeader.nextRecordOffset = free
        free = oldCompact.offsetInPage
    }
}
