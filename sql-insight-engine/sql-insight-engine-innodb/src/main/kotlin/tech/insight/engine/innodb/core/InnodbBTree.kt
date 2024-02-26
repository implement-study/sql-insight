package tech.insight.engine.innodb.core

import tech.insight.core.bean.Index
import tech.insight.core.bean.InsertRow
import tech.insight.core.bean.Row
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
     * b tree can find the row
     *
     */
    fun findByKey(key: IndexKey): Row
}
