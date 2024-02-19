package tech.insight.core.engine.filler

import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr
import com.alibaba.druid.sql.visitor.SQLASTVisitor


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/

class IntegerVisitor(private var intAction: (Int) -> Unit) : SQLASTVisitor {
    override fun endVisit(x: SQLIntegerExpr) {
        intAction.invoke(x.number.toInt())
    }
}
