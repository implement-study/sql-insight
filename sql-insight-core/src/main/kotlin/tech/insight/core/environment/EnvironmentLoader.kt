package tech.insight.core.environment

import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.core.annotation.Temporary
import tech.insight.core.bean.Database
import tech.insight.core.bean.Table
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.extension.SqlInsightConfig
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL
import java.util.*


/**
 *
 * [TableLoader.loadTable]  must after [DatabaseLoader.loadDatabase]
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
object TableLoader {
    private const val TABLE_SUFFIX = ".frm"
    fun loadTable(): List<Table> {
        val home = GlobalContext[DefaultProperty.DATA_DIR]
        val dbArray = File(home).listFiles { obj: File -> obj.isDirectory() } ?: return emptyList()
        val tableList: MutableList<Table> = ArrayList()
        for (dbFile in dbArray) {
            val frmFileArray = dbFile.listFiles { f: File -> f.getName().endsWith(TABLE_SUFFIX) } ?: continue
            for (frmFile in frmFileArray) {
                tableList.add(loadTableMeta(frmFile))
            }
        }
        return tableList
    }

    public fun loadTableMeta(frmFile: File): Table {
        val frmBytes = FileInputStream(frmFile).readAllBytes()
        val buffer = DynamicByteBuffer.wrap(frmBytes, SqlInsightConfig)
        return buffer.getObject(Table::class.java)
    }

    fun writeTableMeta(table: Table) {
        val frm = File(table.database.dbFolder, "${table.name}.frm")
        val buffer = DynamicByteBuffer.allocate(SqlInsightConfig)
        buffer.appendObject(table)
        frm.writeBytes(buffer.toBytes())
    }

}


object DatabaseLoader {
    fun loadDatabase(): List<Database> {
        val home = GlobalContext[DefaultProperty.DATA_DIR]
        val dbArray = File(home).listFiles { obj: File -> obj.isDirectory() } ?: return emptyList()
        return dbArray.map { analysisDatabase(it) }.toList()
    }

    @Temporary("database meta info?")
    private fun analysisDatabase(databaseDir: File): Database {
        return Database(databaseDir.name)
    }
}


/**
 * if you want register your custom engine.
 * add your engine absolute class name in META-INF/engine.properties
 * engine=classname1,classname2,
 *
 */
object EngineLoader {
    private const val ENGINE_RESOURCE_LOCATION = "META-INF/engine.properties"
    fun loadEngine(): List<StorageEngine> {
        val engineList: MutableList<StorageEngine> = ArrayList()

        val urls: Enumeration<URL> = javaClass.getClassLoader().getResources(ENGINE_RESOURCE_LOCATION)
        while (urls.hasMoreElements()) {
            val engine = Properties()
            val url: URL = urls.nextElement()
            url.openConnection().getInputStream()?.use { inputStream: InputStream -> engine.load(inputStream) }
            val engineNames = engine.getProperty("engine")
            val engineNameArray = engineNames.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (name in engineNameArray) {
                if (name.isEmpty()) {
                    continue
                }
                engineList.add(reflectEngine(name))
            }
        }
        return engineList
    }

    private fun reflectEngine(name: String): StorageEngine {
        val engineClass = Class.forName(name)
        val constructor = engineClass.getConstructor()
        return constructor.newInstance() as StorageEngine

    }
}
