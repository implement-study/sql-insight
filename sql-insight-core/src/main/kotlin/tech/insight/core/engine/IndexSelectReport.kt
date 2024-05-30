package tech.insight.core.engine

import tech.insight.core.bean.Index
import tech.insight.core.bean.condition.QueryCondition
import tech.insight.core.plan.ExplainType


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class IndexSelectReport(val index: Index, val queryCondition: QueryCondition) : Comparable<IndexSelectReport> {


    /**
     * select the index will cost (estimated)
     */
    fun cost(): Int {
        return 0
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
