package org.gongxuanzhang.mysql

import org.gongxuanzhang.mysql.core.result.SingleRowResult


fun SingleRowResult.destructuringEquals(other: Collection<String>?): Boolean {
    if (this.data.size != other?.size) {
        return false
    }
    val selectData = this.data.map { jsonObject -> jsonObject.getObject(this.head[0], String::class.java) }
    return selectData.chaosEquals(other)
}
