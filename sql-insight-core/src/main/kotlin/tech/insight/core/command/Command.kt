package tech.insight.core.command

import com.alibaba.druid.sql.ast.SQLStatement
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement
import com.alibaba.druid.sql.ast.statement.SQLDropDatabaseStatement
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement
import tech.insight.core.bean.Always
import tech.insight.core.bean.Table
import tech.insight.core.bean.Where
import tech.insight.core.bean.condition.Expression


/**
 * the command file sealed all of type command
 * @author gongxuanzhangmelt@gmail.com
 */
sealed interface Command {
    val statement: SQLStatement
    val sql: String
}


sealed class DDLCommand(override val sql: String, override val statement: SQLStatement) : Command

sealed class DMLCommand(override val sql: String, override val statement: SQLStatement) : Command

sealed class CreateCommand(sql: String, statement: SQLStatement) : DDLCommand(sql, statement)
sealed class DropCommand(sql: String, statement: SQLStatement) : DDLCommand(sql, statement)
sealed class AlterCommand(sql: String, statement: SQLStatement) : DDLCommand(sql, statement)

class CreateDatabase(sql: String, statement: SQLCreateDatabaseStatement) : CreateCommand(sql, statement) {
    var ifNotExists = false
    lateinit var dbName: String
}

class CreateTable(sql: String, statement: SQLCreateDatabaseStatement) : CreateCommand(sql, statement) {
    var ifNotExists = false
    lateinit var table: Table
}

class DropDatabase(sql: String, statement: SQLDropDatabaseStatement) : DropCommand(sql, statement) {
    var ifIsExists = false
    lateinit var dbName: String
}

class DropTable(sql: String, statement: SQLDropTableStatement) : DropCommand(sql, statement) {
    val dropTables: MutableList<Table> = ArrayList()
    var ifExists = false
}


class DeleteCommand(sql: String, statement: SQLStatement) : DMLCommand(sql, statement) {
    lateinit var table: Table

    var where: Where = Always
}

class InsertCommand(sql: String, statement: SQLStatement) : DMLCommand(sql, statement)
class SelectCommand(sql: String, statement: SQLStatement) : DMLCommand(sql, statement)

class UpdateCommand(sql: String, statement: SQLStatement) : DMLCommand(sql, statement) {
    lateinit var table: Table

    val updateField: MutableMap<String, Expression> = HashMap()

    var where: Where = Always
}









