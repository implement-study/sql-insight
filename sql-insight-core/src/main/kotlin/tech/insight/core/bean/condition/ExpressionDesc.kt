package tech.insight.core.bean.condition


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class ExpressionDesc(val expression: String) {
    

    /**
     * the expression is impossible to calculate
     */
    fun impossible(): Boolean {
        return false
    }

    /**
     * appear identifiers in the expression
     */
    fun appearIdentifiers(): List<String> {
        return emptyList()
    }
    
    


}
