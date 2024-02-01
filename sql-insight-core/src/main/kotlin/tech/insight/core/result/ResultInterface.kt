package tech.insight.core.result


interface ResultInterface


class MessageResultInterface(private val message: String) : ResultInterface {
    override fun toString(): String {
        return "result : $message"
    }
}

class ExceptionResultInterface(val e: Exception) : ResultInterface




