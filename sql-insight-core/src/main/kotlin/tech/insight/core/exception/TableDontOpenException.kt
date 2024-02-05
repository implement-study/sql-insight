package tech.insight.core.exception

import tech.insight.core.bean.Table

class TableDontOpenException(belongTo: Table) : Exception("table ${belongTo.name} dont open")
