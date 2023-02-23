package org.gongxuanzhang.mysql

import org.gongxuanzhang.mysql.core.result.Result
import org.gongxuanzhang.mysql.service.analysis.ast.SubSqlAnalysis
import org.gongxuanzhang.mysql.service.executor.Executor
import org.gongxuanzhang.mysql.service.token.SqlTokenizer


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/

fun String.doSql(): Result {
    val tokenAnalysis = SubSqlAnalysis()
    tokenAnalysis.init()
    val tokenizer = SqlTokenizer(this)
    val process = tokenizer.process()
    val executor: Executor = tokenAnalysis.analysis(process)
    return executor.doExecute()
}

