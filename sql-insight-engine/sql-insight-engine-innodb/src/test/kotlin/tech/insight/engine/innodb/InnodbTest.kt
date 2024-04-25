package tech.insight.engine.innodb

import tech.insight.core.engine.SqlPipeline
import tech.insight.core.result.ExceptionResult
import tech.insight.core.result.ResultInterface
import tech.insight.share.data.dropDatabase


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/

fun dropDb(dbName: String) {
    SqlPipeline.executeSql(dropDatabase(dbName, true))
}


inline fun <reified T> assertSqlThrows(execute: () -> ResultInterface) {
    val result = execute.invoke()
    assert(result is ExceptionResult)
    assert(T::class.java.isAssignableFrom((result as ExceptionResult).exception.javaClass))
}
