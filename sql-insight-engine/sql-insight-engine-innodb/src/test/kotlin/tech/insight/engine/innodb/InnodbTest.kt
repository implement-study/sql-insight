package tech.insight.engine.innodb

import tech.insight.core.engine.SqlPipeline
import tech.insight.share.data.dropDatabase


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/

fun dropDb(dbName: String) {
    SqlPipeline.executeSql(dropDatabase(dbName, true))
}
