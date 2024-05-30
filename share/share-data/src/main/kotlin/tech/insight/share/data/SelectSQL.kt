package tech.insight.share.data


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/

fun selectAll(tableName: String, databaseName: String? = null): String {
    val db = prepareDbPre(databaseName)
    return "select * from $db$tableName"
}


fun selectWhereId(id: Int, tableName: String, databaseName: String? = null): String {
    val db = prepareDbPre(databaseName)
    return "select * from $db$tableName where id = $id"
}


fun selectComplexWhere(id: Int, name: String, tableName: String, databaseName: String? = null): String {
    val db = prepareDbPre(databaseName)
    return "select * from $db$tableName where id = $id and name = '$name'"
}

