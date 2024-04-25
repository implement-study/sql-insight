package tech.insight.share.data


/**
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
fun dropDatabase(databaseName: String, ifExists: Boolean = false): String {
    return "DROP DATABASE ${if (ifExists) "IF EXISTS" else ""} $databaseName;"
}

fun dropTable(
    tableName: String,
    databaseName: String? = null,
    ifExists: Boolean = false
): String {
    val databaseNamePre = if (databaseName == null) {
        ""
    } else {
        "$databaseName."
    }
    return """
        DROP TABLE ${if (ifExists) "IF EXISTS" else ""} $databaseNamePre$tableName;
    """.trimIndent()
}



