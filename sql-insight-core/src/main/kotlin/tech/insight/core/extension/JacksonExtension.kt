package tech.insight.core.extension

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.FileInputStream


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
val mapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

fun Any.json(): String {
    return mapper.writeValueAsString(this)
}

fun Any.tree(): ObjectNode {
    return mapper.valueToTree(this)
}

inline fun <reified T> String.toObject(): T {
    return mapper.readValue(this, T::class.java)
}

fun String.tree(): ObjectNode {
    return mapper.readValue(this, ObjectNode::class.java)
}

inline fun <reified T> FileInputStream.toObject(): T {
    return mapper.readValue(this, T::class.java)
}

inline fun <reified T> String.toArray(): List<T> {
    return mapper.readValue<List<T>>(this)
}

inline fun <reified T> FileInputStream.toArray(): List<T> {
    return mapper.readValue<List<T>>(this)
}



