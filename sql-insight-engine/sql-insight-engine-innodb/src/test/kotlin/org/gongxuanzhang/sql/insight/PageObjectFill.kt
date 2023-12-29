package org.gongxuanzhang.sql.insight

import com.alibaba.fastjson2.JSONObject
import kotlin.random.Random


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/

fun <T> fillNumber(t: T): T {
    val jsonObject = JSONObject.parse(JSONObject.toJSONString(t))
    for ((key, value) in jsonObject) {
        if (value is Number) {
            jsonObject[key] = Random.nextInt()
        }
    }
    return jsonObject.toJavaObject(t!!::class.java)
}


