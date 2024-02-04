package tech.insight.core.extension

import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import org.gongxuanzhang.easybyte.core.ReadConverter
import org.gongxuanzhang.easybyte.core.WriteConverter
import org.gongxuanzhang.easybyte.core.environment.ObjectConfig
import tech.insight.core.bean.Column
import tech.insight.core.bean.DataType
import tech.insight.core.bean.Database
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.*
import tech.insight.core.environment.DatabaseManager
import tech.insight.core.environment.EngineManager
import java.nio.ByteBuffer


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
            val buffer = DynamicByteBuffer.wrap(bytes,SqlInsightConfig)
            name = buffer.string
            database = DatabaseManager.require(buffer.string)
            columnList = buffer.getCollection(Column::class.java)
            engine = EngineManager.selectEngine(buffer.string)
            comment = buffer.string
        }
        return table
    }


}

object DataTypeReadConverter : ReadConverter<DataType> {
    override fun toObject(bytes: ByteArray, length: Int): DataType {
        val byte = bytes[0]
        return DataType.entries[byte.toInt()]
    }

}

object DataTypeWriteConverter : WriteConverter<DataType> {

    override fun toBytes(v: DataType): ByteArray {
        return byteArrayOf(v.ordinal.toByte())
    }
}

object ValueReadConverter : ReadConverter<Value<*>> {
    override fun toObject(bytes: ByteArray, length: Int): Value<*> {
        val buffer = ByteBuffer.wrap(bytes)
        val type = buffer.get().toInt()
        if (type == 1) {
            return buffer.get().toInt().let { if (it == 1) ValueTrue else ValueFalse }
        }
        if (type == 2) {
            val charLength = buffer.getInt()
            val content = String(ByteArray(buffer.getInt()).also { buffer[it] })
            return ValueChar(content, charLength)
        }
        if (type == 3) {
            return ValueInt(buffer.getInt())
        }
        if (type == 4) {
            return ValueNull
        }
        if (type == 5) {
            val string = String(ByteArray(buffer.getInt()).also { buffer[it] })
            return ValueVarchar(string)
        }
        throw IllegalArgumentException("value type :$type error")
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

object ColumnWriteConverter : WriteConverter<Column> {


    override fun toBytes(v: Column): ByteArray {
        val allocate = DynamicByteBuffer.allocate(SqlInsightConfig)
        allocate.appendString(v.name)
        allocate.appendObject(v.dataType)
        allocate.appendInt(v.length)
        allocate.appendString(v.comment)
        allocate.appendObject(v.defaultValue,ValueWriteConverter)
        val flag = v.autoIncrement append v.notNull append v.primaryKey append v.unique append v.variable
        allocate.append(flag.toByte())
        return allocate.toBytes()
    }

}

object ColumnReadConverter : ReadConverter<Column> {

    override fun toObject(bytes: ByteArray, length: Int): Column {
        val buffer = DynamicByteBuffer.wrap(bytes,SqlInsightConfig)
        val col = Column()
        col.name = buffer.string
        col.dataType = buffer.getObject(DataType::class.java)
        col.length = buffer.int
        col.comment = buffer.string
        col.defaultValue = buffer.getObject(ValueReadConverter)
        val flag = buffer.get().toInt()
        flag.let {
            col.variable = it and 1 == 1
            it shr 1
        }.let {
            col.unique = it and 1 == 1
            it shr 1
        }.let {
            col.primaryKey = it and 1 == 1
            it shr 1
        }.let {
            col.notNull = it and 1 == 1
            it shr 1
        }.let {
            col.autoIncrement = it and 1 == 1
            it shr 1
        }
        return col
    }

}

