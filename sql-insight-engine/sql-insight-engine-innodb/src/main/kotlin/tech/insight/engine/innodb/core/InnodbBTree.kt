package tech.insight.engine.innodb.core

import tech.insight.core.bean.Index
import tech.insight.core.bean.InsertRow
import tech.insight.engine.innodb.page.IndexKey
import tech.insight.engine.innodb.page.InnoDbPage

/**
 * innodb b+ tree
 *
 * @author gongxuanzhangmelt@gmail.com
 */
interface InnodbBTree : Index {
    /**
     * insert a row.
     */
    override fun insert(row: InsertRow)


    /**
     * b tree can find the page where the key in,also can't
     *
     */
    fun findByKey(key: IndexKey): InnoDbPage?
}
