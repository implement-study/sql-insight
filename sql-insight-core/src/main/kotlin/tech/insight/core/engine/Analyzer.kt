package tech.insight.core.engine

import com.alibaba.druid.sql.SQLUtils
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement
import com.alibaba.druid.sql.visitor.SQLASTVisitor
import tech.insight.core.command.Command
import tech.insight.core.command.CreateDatabase


/**
 * analysis sql to command
 *
 * @author gongxuanzhangmelt@gmail.com
 */
interface Analyzer {
    /**
     * analysis sql to a wrapped sql
     *
     * @return the command
     */
    fun analysisSql(sql: String): Command
}


/**
 * delegate to druid sql AST.
 * command type visitor wrap the sql to command type.
 * filler visitor fill the command field.
 *
 */
object DruidAnalyzer : Analyzer {

    override fun analysisSql(sql: String): Command {
        return CommandTypeVisitor(sql).command.also { DispatcherFiller.fill(it) }
    }
}

class CommandTypeVisitor(private val sql: String) : SQLASTVisitor {
    private val sqlStatement = SQLUtils.parseSingleMysqlStatement(sql)
    lateinit var command: Command

    init {
        sqlStatement.accept(this)
    }

    override fun visit(x: SQLCreateDatabaseStatement): Boolean {
        this.command = CreateDatabase(sql, x)
        return false;
    }

//    override fun endVisit(x: SQLDropDatabaseStatement): Boolean {
//        command = DropDatabase(sql)
//        x.accept(command)
//        return false
//    }
//
//    override fun endVisit(x: SQLDeleteStatement): Boolean {
//        command = Delete(sql)
//        x.accept(command)
//        return false
//    }
//
//    override fun endVisit(x: SQLDropTableStatement): Boolean {
//        command = DropTable(sql)
//        x.accept(command)
//        return false
//    }
//
//    override fun endVisit(x: SQLCreateTableStatement): Boolean {
//        command = CreateTable(sql)
//        x.accept(command)
//        return false
//    }
//
//    override fun endVisit(x: SQLInsertStatement): Boolean {
//        command = Insert(sql,x)
//        x.accept(command)
//        return false
//    }
//
//    override fun endVisit(x: SQLUpdateStatement): Boolean {
//        command = Update(sql)
//        x.accept(command)
//        return false
//    }
//
//    override fun endVisit(x: SQLSelectQueryBlock): Boolean {
//        command = Select(sql)
//        x.accept(command)
//        return false
//    }
}


