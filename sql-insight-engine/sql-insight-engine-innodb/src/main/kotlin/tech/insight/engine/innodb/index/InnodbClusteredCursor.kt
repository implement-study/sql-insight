package tech.insight.engine.innodb.index

import tech.insight.core.bean.Always
import tech.insight.core.bean.Cursor
import tech.insight.core.bean.Row
import tech.insight.core.bean.Where
import tech.insight.core.environment.Session
import tech.insight.core.logging.Logging
import java.io.RandomAccessFile


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InnodbClusteredCursor(index: InnodbIndex, private val session: Session) : Logging(), Cursor {
    val file: RandomAccessFile = RandomAccessFile(index.file, "rw")

    val root = index.rootPage

    var condition: Where = Always

    override fun close() {
        file.close()
        debug("close the cursor session id:[${session.id}]")
    }

    override fun hasNext(): Boolean {

        TODO("Not yet implemented")
    }

    override fun next(): Row {
        TODO("Not yet implemented")
    }


}
