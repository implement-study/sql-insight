package tech.insight.core.event

import tech.insight.core.bean.Database
import tech.insight.core.bean.Table


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class CreateDatabaseEvent(database: Database) : InsightEvent(database) {
    val database: Database
        get() = source as Database
}


class CreateTableEvent(table: Table) : InsightEvent(table) {
    val table: Table
        get() = source as Table
}
