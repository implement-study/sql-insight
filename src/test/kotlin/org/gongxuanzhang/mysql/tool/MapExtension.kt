package org.gongxuanzhang.mysql.tool

import com.alibaba.fastjson2.JSONObject


fun <K, V> Map<K, V>.toJSONObject(): JSONObject {
    return JSONObject(this)
}
