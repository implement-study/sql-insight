package tech.insight.core.bean

import java.util.*
import tech.insight.core.bean.value.Value
import tech.insight.core.util.truncateStringIfTooLong


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
open class NormalRow(override val rowId: Long, val belongTo: Table) : Row, SQLBean, Iterable<Cell> {

    /**
     * subclass can manipulate this list
     */
    protected val candidateValues = mutableListOf<Value<*>>()

    override val values: List<Value<*>>
        get() = candidateValues.toList()

    override fun getValueByColumnName(colName: String): Value<*> {
        val index = belongTo.getColumnIndexByName(colName)
        return candidateValues[index]
    }

    override fun belongTo(): Table {
        return belongTo
    }

    override fun iterator(): Iterator<Cell> {
        return Iter()
    }

    private inner class Iter : Iterator<Cell> {
        var cursor = 0
        override fun hasNext(): Boolean {
            return cursor != candidateValues.size
        }

        override fun next(): Cell {
            val i = cursor
            if (i >= candidateValues.size) {
                throw NoSuchElementException()
            }
            cursor = i + 1
            return Cell(belongTo.columnList[i], candidateValues[i])
        }
    }

    override fun toString(): String {
        val stringJoiner = StringJoiner("|", "|", "|")
        for (value in values) {
            stringJoiner.add(truncateStringIfTooLong(value.toString(), 10))
        }
        return stringJoiner.toString()
    }
}
