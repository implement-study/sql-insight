package tech.insight.core.engine

import tech.insight.core.bean.Index
import tech.insight.core.bean.condition.QueryCondition
import tech.insight.core.engine.IdentifierSelectType.COMPLEX
import tech.insight.core.engine.IdentifierSelectType.CONST
import tech.insight.core.engine.IdentifierSelectType.IMPOSSIBLE
import tech.insight.core.engine.IdentifierSelectType.MULTI_CONST
import tech.insight.core.engine.IdentifierSelectType.RANGE
import tech.insight.core.plan.ExplainType


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class IndexSelectReport(val index: Index, val queryCondition: QueryCondition) : Comparable<IndexSelectReport> {

    private var matchDegree = 0

    init {
        //  todo cost calc
    }


    /**
     * select the index will cost (estimated)
     */
    private fun cost(): Int {
        return Int.MAX_VALUE - matchDegree
    }

    fun type(): ExplainType {
        return ExplainType.ALL
    }

    override fun compareTo(other: IndexSelectReport): Int {
        return cost().compareTo(other.cost())
    }

    companion object {

    }

}
