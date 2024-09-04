package tech.insight.core.extension

import java.nio.ByteBuffer
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import org.gongxuanzhang.easybyte.core.ReadConverter
import org.gongxuanzhang.easybyte.core.WriteConverter
import org.gongxuanzhang.easybyte.core.environment.ObjectConfig
import tech.insight.buffer.toByteArray
import tech.insight.core.bean.Column
import tech.insight.core.bean.DataType
import tech.insight.core.bean.Database
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueBoolean
import tech.insight.core.bean.value.ValueChar
import tech.insight.core.bean.value.ValueFalse
import tech.insight.core.bean.value.ValueInt
import tech.insight.core.bean.value.ValueNull
import tech.insight.core.bean.value.ValueTrue
import tech.insight.core.bean.value.ValueVarchar
import tech.insight.core.environment.DatabaseManager
import tech.insight.core.environment.EngineManager


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/

object SqlInsightConfig : ObjectConfig() {
    init {
        registerReadConverter(DatabaseReaderConverter)
        registerWriteConverter(DatabaseWriteConverter)

        registerReadConverter(DataTypeReadConverter)
        registerWriteConverter(DataTypeWriteConverter)
        registerReadConverter(ColumnReadConverter)
        registerWriteConverter(ColumnWriteConverter)
        registerReadConverter(TableReadConverter)
        registerWriteConverter(TableWriteConverter)
    }
}


object DatabaseReaderConverter : ReadConverter<Database> {

    override fun toObject(bytes: ByteArray, length: Int): Database {
        return DatabaseManager.require(String(bytes))
    }

}

object DatabaseWriteConverter : WriteConverter<Database> {

    override fun toBytes(v: Database): ByteArray {
        return v.name.toByteArray()
    }
}

object TableWriteConverter : WriteConverter<Table> {


    override fun toBytes(v: Table): ByteArray {
        with(v) {
            val buffer = DynamicByteBuffer.allocate(SqlInsightConfig)
            buffer.appendString(name)
            buffer.appendString(databaseName)
            buffer.appendCollection(columnList)
            buffer.appendString(engine.name)
            buffer.appendString(comment)
            return buffer.toBytes()
        }
    }

}

object TableReadConverter : ReadConverter<Table> {

    override fun toObject(bytes: ByteArray, length: Int): Table {
        val table = Table()
        with(table) {
            val buffer = DynamicByteBuffer.wrap(bytes, SqlInsightConfig)
            name = buffer.string
            database = DatabaseManager.require(buffer.string)
            columnList = buffer.getCollection(Column::class.java)
            engine = EngineManager.selectEngine(buffer.string)
            comment = buffer.string
        }
        return table
    }


}



object ValueWriteConverter : WriteConverter<Value<*>> {

    override fun toBytes(v: Value<*>): ByteArray {
        return when (v) {
            is ValueBoolean -> byteArrayOf(1, if (v.source) 1 else 0)
            is ValueChar -> {
                val result: MutableList<Byte> = mutableListOf(2)
                result.addAll(v.length.toByteArray().toList())
                val sourceBytes = v.source.toByteArray()
                //   add length
                result.addAll(sourceBytes.size.toByteArray().toList())
                //   add source
                result.addAll(sourceBytes.toList())
                result.toByteArray()
            }

            is ValueInt -> {
                val result: MutableList<Byte> = mutableListOf(3)
                result.addAll(v.source.toByteArray().toList())
                result.toByteArray()
            }

            ValueNull -> {
                byteArrayOf(4)
            }

            is ValueVarchar -> {
                val result: MutableList<Byte> = mutableListOf(5)
                val sourceBytes = v.source.toByteArray()
                result.addAll(sourceBytes.size.toByteArray().toList())
                result.addAll(sourceBytes.toList())
                result.toByteArray()
            }
        }
    }
}


