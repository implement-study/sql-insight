package tech.insight.engine.innodb.core

import tech.insight.core.bean.Index
import tech.insight.core.bean.InsertRow

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
}
