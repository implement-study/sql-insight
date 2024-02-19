package tech.insight.core.result

import tech.insight.core.bean.Row


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class SelectResult(val result: List<Row>) : ResultInterface {

    override fun toString(): String {
        val sb = StringBuilder()
        result.forEach { sb.appendLine(it) }
        return sb.toString()
    }
}
