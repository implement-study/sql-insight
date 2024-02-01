package tech.insight.core.engine.filler

import com.alibaba.druid.sql.visitor.SQLASTVisitor

/**
 * fill object field by visitor
 */
interface Filler<in O> : SQLASTVisitor {
    fun fill(any: O)
}
