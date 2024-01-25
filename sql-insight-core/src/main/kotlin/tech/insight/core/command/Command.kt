package tech.insight.core.command

import com.alibaba.druid.sql.ast.SQLStatement
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement


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


class InsertCommand(sql: String, statement: SQLStatement) : DMLCommand(sql, statement)
class SelectCommand(sql: String, statement: SQLStatement) : DMLCommand(sql, statement)
class UpdateCommand(sql: String, statement: SQLStatement) : DMLCommand(sql, statement)
class DeleteCommand(sql: String, statement: SQLStatement) : DMLCommand(sql, statement)

class CreateDatabase(sql: String, statement: SQLCreateDatabaseStatement) : CreateCommand(sql, statement) {
    var ifNotExists = false
    lateinit var dbName: String
}



