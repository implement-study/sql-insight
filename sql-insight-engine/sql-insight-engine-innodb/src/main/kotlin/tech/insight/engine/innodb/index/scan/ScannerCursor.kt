package tech.insight.engine.innodb.index.scan

import tech.insight.core.bean.Cursor
import tech.insight.core.command.SelectCommand
import tech.insight.core.plan.ExplainType
import tech.insight.engine.innodb.index.InnodbIndex


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
interface ScannerCursor : Cursor {

    /**
     * cursor from index
     */
    val index: InnodbIndex

    /**
     * scan pattern
     */
    val explainType: ExplainType


    companion object {
        fun create(index: InnodbIndex, command: SelectCommand, explainType: ExplainType): ScannerCursor {
            when (explainType) {
                ExplainType.ALL -> return AllScannerCursor(index)
                else -> throw IllegalArgumentException("not support explain type $explainType")
            }
        }
    }
}
