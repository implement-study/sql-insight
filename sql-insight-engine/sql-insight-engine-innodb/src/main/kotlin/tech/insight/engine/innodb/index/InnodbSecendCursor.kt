package tech.insight.engine.innodb.index

import java.io.RandomAccessFile
import tech.insight.core.bean.Cursor
import tech.insight.core.bean.Row
import tech.insight.core.environment.Session
import tech.insight.core.logging.Logging


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InnodbSecendCursor(index: InnodbIndex, private val session: Session) : Logging(), Cursor {

    val file: RandomAccessFile = RandomAccessFile(index.file, "rw")

    val root = index.rootPage

    override fun close() {
        file.close()
        debug { "close the cursor session id:[${session.id}]" }
    }

    override fun hasNext(): Boolean {

        TODO("Not yet implemented")
    }

    override fun next(): Row {
        TODO("Not yet implemented")
    }


}
