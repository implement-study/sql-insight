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
    userRecords.addRecord(newCompact)
    pre.linkRecord(newCompact)
    newCompact.linkRecord(next)
    oldCompact.recordHeader.deleteMask = true
    oldCompact.recordHeader.nextRecordOffset = pageHeader.deleteStart - oldCompact.offsetInPage
    pageHeader.garbage += oldCompact.length()
}
