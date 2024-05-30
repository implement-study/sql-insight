package tech.insight.core.engine.filler

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr
import com.alibaba.druid.sql.ast.expr.SQLCharExpr
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr
import com.alibaba.druid.sql.visitor.SQLASTVisitor
import tech.insight.core.bean.Always
import tech.insight.core.bean.ExpressionVisitor
import tech.insight.core.bean.Never
import tech.insight.core.bean.SQLBean
import tech.insight.core.bean.Where
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.environment.EngineManager


interface BeanFiller<in B : SQLBean> : SQLASTVisitor


class CommentVisitor(private val commentAction: (String) -> Unit) : SQLASTVisitor {
    override fun endVisit(x: SQLCharExpr) {
        commentAction.invoke(x.text)
    }
}

class EngineVisitor(private val engineAction: (StorageEngine) -> Unit) : SQLASTVisitor {
    override fun endVisit(x: SQLCharExpr) {
        val engine = EngineManager.selectEngine(x.text)
        engineAction.invoke(engine)
    }
}


class WhereVisitor(private val whereAction: (Where) -> Unit) : SQLASTVisitor {
    override fun visit(x: SQLBinaryOpExpr): Boolean {
        val visitor = ExpressionVisitor { whereAction.invoke(Where(it)) }
        x.accept(visitor)
        return false
    }

    override fun visit(x: SQLBooleanExpr): Boolean {
        val where = if (x.booleanValue) Always else Never
        whereAction.invoke(where)
        return false
    }

    override fun visit(x: SQLIntegerExpr): Boolean {
        val where = if (x.number.toInt() != 0) Always else Never
        whereAction.invoke(where)
        return false
    }
}



