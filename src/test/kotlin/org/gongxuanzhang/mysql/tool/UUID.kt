package org.gongxuanzhang.mysql.tool

import java.util.*


/**
 * @author gongxuanzhang
 */

fun randomDatabase(): String {
    val uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 5)
    //  保证首尾不是数字
    return "a$uuid"
}
