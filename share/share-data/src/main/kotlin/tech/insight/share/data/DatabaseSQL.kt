package tech.insight.share.data


/**
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/

fun dropDatabase(databaseName: String, ifExists: Boolean = false): String {
    return "DROP DATABASE ${if (ifExists) "IF EXISTS" else ""} $databaseName;"
}

fun createDatabase(databaseName: String, ifNotExists: Boolean = false): String {
    return "CREATE DATABASE ${if (ifNotExists) "IF NOT EXISTS" else ""} $databaseName;"
}

fun useDatabase(databaseName: String): String {
    return "USE $databaseName"
}


