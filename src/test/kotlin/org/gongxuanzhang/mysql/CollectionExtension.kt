package org.gongxuanzhang.mysql


fun <T : Comparable<T>> Collection<T>.chaosEquals(other: Collection<T>): Boolean {
    if (this.size != other.size) {
        return false
    }
    val thisData = this.sorted()
    val otherData = other.sorted()
    for (index in thisData.indices) {
        if (thisData[index] != otherData[index]) {
            return false
        }
    }
    return true
}

