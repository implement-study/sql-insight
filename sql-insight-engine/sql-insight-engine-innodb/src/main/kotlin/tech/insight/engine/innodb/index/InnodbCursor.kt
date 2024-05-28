package tech.insight.engine.innodb.index

import tech.insight.core.bean.Cursor
import tech.insight.core.bean.Where


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
interface InnodbCursor : Cursor {

    /**
     * query condition , subclass should determines whether the index contains a condition
     */
    fun where(): Where

    /**
     * if next result gather than [limit] should return null
     */
    fun limit(): Long

    fun skip(): Long

}
