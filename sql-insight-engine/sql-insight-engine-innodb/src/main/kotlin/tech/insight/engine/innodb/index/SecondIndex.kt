//package tech.insight.engine.innodb.index
//
//import tech.insight.core.bean.Column
//import tech.insight.core.bean.Cursor
//import tech.insight.core.bean.InsertRow
//import tech.insight.core.bean.Table
//import tech.insight.core.environment.Session
//import tech.insight.engine.innodb.page.IndexKey
//import tech.insight.engine.innodb.page.InnoDbPage
//import java.io.File
//
///**
// * second index
// *
// * @author gongxuanzhangmelt@gmail.com
// */
//class SecondIndex protected constructor(table: Table) : InnodbIndex() {
//    override fun insert(row: InsertRow) {
//        TODO("Not yet implemented")
//    }
//
//    override fun findByKey(key: IndexKey): InnoDbPage? {
//        TODO("Not yet implemented")
//    }
//
//    override fun rndInit() {
//        TODO("Not yet implemented")
//    }
//
//    override val id: Int
//        get() = TODO("Not yet implemented")
//
//    override fun find(session: Session): Cursor {
//        TODO("Not yet implemented")
//    }
//
//    override val name: String
//        get() = TODO("Not yet implemented")
//    override val file: File
//        get() = TODO("Not yet implemented")
//
//    override fun columns(): List<Column> {
//        TODO("Not yet implemented")
//    }
//}
