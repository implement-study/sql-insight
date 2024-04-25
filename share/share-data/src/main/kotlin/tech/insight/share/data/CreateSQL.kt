package tech.insight.share.data


/**
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
fun createDatabase(databaseName: String, ifNotExists: Boolean = false): String {
    return "CREATE DATABASE ${if (ifNotExists) "IF NOT EXISTS" else ""} $databaseName;"
}

fun createTable(
    tableName: String,
    databaseName: String? = null,
    comment: String = tableName,
    ifNotExists: Boolean = false
): String {
    val databaseNamePre = if (databaseName == null) {
        ""
    } else {
        "$databaseName."
    }
    return """
        create table ${if (ifNotExists) "IF NOT EXISTS" else ""} $databaseNamePre$tableName(
        id int primary key auto_increment,
        name varchar not null,
        gender varchar(20) default '男' not null comment '性别',
        id_card char UNIQUE
        ) comment = '$comment'
    """.trimIndent()
}



