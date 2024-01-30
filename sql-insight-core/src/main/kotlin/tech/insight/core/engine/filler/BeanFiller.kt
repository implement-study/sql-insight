package tech.insight.core.engine.filler

import com.alibaba.druid.sql.ast.expr.SQLCharExpr
import com.alibaba.druid.sql.visitor.SQLASTVisitor
import tech.insight.core.bean.SQLBean


interface BeanFiller<in B : SQLBean> : SQLASTVisitor



class CommentVisitor(private val commentAction: (String) -> Unit) : SQLASTVisitor {
    override fun endVisit(x: SQLCharExpr) {
        commentAction.invoke(x.text)
    }
}

class EngineVisitor(private val engineAction: (String) -> Unit) : SQLASTVisitor {
    override fun endVisit(x: SQLCharExpr) {
        engineAction.invoke(x.text)
    }
}



