package tech.insight.core.engine.filler

import com.alibaba.druid.sql.ast.expr.SQLCharExpr
import com.alibaba.druid.sql.visitor.SQLASTVisitor
import tech.insight.core.bean.SQLBean
import tech.insight.core.environment.EngineManager
import tech.insight.core.engine.storage.StorageEngine


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



