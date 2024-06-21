package tech.insight.core.bean

import tech.insight.core.bean.value.Value

data class Cell(val column: Column, val value: Value<*>)
