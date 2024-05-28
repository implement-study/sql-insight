package tech.insight.share.data


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/

fun selectAll(tableName: String, databaseName: String? = null): String {
    val db = prepareDbPre(databaseName)
    return "select * from $db$tableName"
}
