package tech.insight.core.engine.filler

import com.alibaba.druid.sql.ast.SQLLimit
import com.alibaba.druid.sql.ast.SQLOrderBy
import com.alibaba.druid.sql.ast.SQLOrderingSpecification
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock
import com.alibaba.druid.sql.visitor.SQLASTVisitor
import tech.insight.core.annotation.Temporary
import tech.insight.core.bean.OrderBy
import tech.insight.core.bean.Table
import tech.insight.core.command.SelectCommand


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class SelectFiller : ExplainableFiller<SelectCommand>() {


    override fun visit(x: SQLSelectQueryBlock): Boolean {
        x.from.accept(FromVisitor { command.table = it })
        x.where.accept(WhereVisitor { command.where = it })
        x.orderBy?.accept(OrderByVisitor { command.orderBy = it })
        x.limit?.accept(this)
        return false
    }

    override fun visit(x: SQLLimit): Boolean {
        x.offset?.accept(IntegerVisitor { command.limit.offset = it })
        x.rowCount?.accept(IntegerVisitor { command.limit.rowCount = it })
        return false
    }

}


class FromVisitor(private var tableAction: (Table) -> Unit) : SQLASTVisitor {
    @Temporary(detail = "how to deal join condition?")
    override fun visit(x: SQLJoinTableSource): Boolean {
        throw UnsupportedOperationException("join select don't support")
//        x.left.accept(this)
//        x.right.accept(this)
    }

    override fun visit(x: SQLExprTableSource): Boolean {
        x.accept(TableSelectVisitor(true) { tableAction.invoke(it!!) })
        return false
    }
}


class OrderByVisitor(private val orderByAction: (OrderBy) -> Unit) : SQLASTVisitor {
    override fun visit(x: SQLOrderBy): Boolean {
        val orderByColumnNames = Array(x.items.size) { "" }
        val asc = BooleanArray(orderByColumnNames.size)
        val items = x.items
        for (i in items.indices) {
            val item = items[i]
            if (item.type == SQLOrderingSpecification.ASC) {
                asc[i] = true
            }
            orderByColumnNames[i] = item.expr.toString()
        }
        orderByAction.invoke(OrderBy(orderByColumnNames, asc))
        return false
    }
}



