package tech.insight.share.data

import javax.swing.text.html.HTML.Tag.P


/**
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/


fun dropTable(
    tableName: String,
    databaseName: String? = null,
    ifExists: Boolean = false
): String {
    val databaseNamePre = prepareDbPre(databaseName)
    return """
        DROP TABLE ${if (ifExists) "IF EXISTS" else ""} $databaseNamePre$tableName;
    """.trimIndent()
}

fun createTable(
    tableName: String,
    databaseName: String? = null,
    comment: String = tableName,
    ifNotExists: Boolean = false
): String {
    val databaseNamePre = prepareDbPre(databaseName)
    return """
        create table ${if (ifNotExists) "IF NOT EXISTS" else ""} $databaseNamePre$tableName(
        id int primary key auto_increment,
        name varchar not null,
        gender varchar(20) default '男' not null comment '性别',
        id_card char UNIQUE
        ) comment = '$comment'
    """.trimIndent()
}


fun insertOneData(tableName: String, databaseName: String? = null): String {
    return insertDataCount(tableName,databaseName,1)
}

fun insertData(tableName: String, databaseName: String? = null): String {
    val databaseNamePre = prepareDbPre(databaseName)
    return """
        insert into $databaseNamePre$tableName (id,name) values
        (1,'a'),
        (2,'b'),
        (null,'c'),
        (null,'b'),
        (null,'c')
    """
}

fun insertDataCount(tableName: String, databaseName: String? = null, count: Int): String {
    val databaseNamePre = prepareDbPre(databaseName)
    val values = (1..count).joinToString(",") { "($it,'a$it')" }
    return """
        insert into $databaseNamePre$tableName (id,name) values 
        $values
    """
}


fun insertBigDataCount(tableName: String, databaseName: String? = null, count: Int): String {
    val databaseNamePre = prepareDbPre(databaseName)
    val values = (1..count).joinToString(",") { "($it,'${"aaaaaaaa".repeat(30)}')" }
    return """
        insert into $databaseNamePre$tableName (id,name) values 
        $values
    """
}

private fun prepareDbPre(databaseName: String?): String {
    val databaseNamePre = if (databaseName == null) {
        ""
    } else {
        "$databaseName."
    }
    return databaseNamePre
}
