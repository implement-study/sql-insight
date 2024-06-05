package tech.insight.core.bean.condition


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class IdentifierDetailsGroup {

    private val identifierDetailsMap = mutableMapOf<String, IdentifierDetails>()


    fun append(identifierDetails: IdentifierDetails) {
        identifierDetailsMap.merge(identifierDetails.name, identifierDetails) { old, new ->
            old.merge(new)
        }
    }


}
