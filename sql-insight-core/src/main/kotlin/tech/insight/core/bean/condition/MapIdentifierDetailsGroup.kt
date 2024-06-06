package tech.insight.core.bean.condition


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class MapIdentifierDetailsGroup : IdentifierDetailsGroup {


    private val identifierDetailsMap = mutableMapOf<String, IdentifierDetails>()


    override fun merge(identifierDetails: IdentifierDetails) {
        identifierDetailsMap.merge(identifierDetails.name, identifierDetails) { old, new ->
            MergeStrategy.getMergeStrategy(old.selectType, new.selectType).merge(old, new)
        }
    }

    override fun merge(otherGroup: IdentifierDetailsGroup) {
        otherGroup.forEach {
            this.merge(it)
        }
    }

    override fun get(identifierName: String): IdentifierDetails? {
        return identifierDetailsMap[identifierName]
    }

    override fun iterator(): Iterator<IdentifierDetails> {
        return identifierDetailsMap.values.iterator()
    }
}
