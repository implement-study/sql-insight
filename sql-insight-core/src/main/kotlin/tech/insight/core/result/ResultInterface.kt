package tech.insight.core.result


interface ResultInterface


class MessageResult(private val message: String) : ResultInterface {
    override fun toString(): String {
        return "result : $message"
    }
}

class ExceptionResult(val e: Exception) : ResultInterface




