package tech.insight.core.result

import tech.insight.core.bean.Table


interface ResultInterface


class MessageResult(private val message: String) : ResultInterface {
    override fun toString(): String {
        return "result : $message"
    }
}

class DeleteResult(private val count: Int, val table: Table) : ResultInterface {
    override fun toString(): String {
        return "result : delete ${table.name} $count rows"
    }
}

class ExceptionResult(val e: Exception) : ResultInterface




