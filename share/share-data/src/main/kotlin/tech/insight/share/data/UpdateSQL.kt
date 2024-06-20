package tech.insight.share.data


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
fun updateAllName(newName: String, tableName: String, databaseName: String? = null): String {
    val db = prepareDbPre(databaseName)
    return "update $db$tableName set name = '$newName'"
}
