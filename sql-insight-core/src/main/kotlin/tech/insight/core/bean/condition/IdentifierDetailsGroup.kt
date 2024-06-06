package tech.insight.core.bean.condition


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
interface IdentifierDetailsGroup : Iterable<IdentifierDetails> {

    fun merge(identifierDetails: IdentifierDetails)

    fun merge(otherGroup: IdentifierDetailsGroup)

    fun get(identifierName: String): IdentifierDetails?
}
