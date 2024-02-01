package tech.insight.core.environment

import tech.insight.core.annotation.Temporary
import tech.insight.core.bean.Table
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.extension.toObject
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL
import java.util.*


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
object TableLoader {
    private const val TABLE_SUFFIX = ".frm"
    fun loadTable(): List<Table> {
        val home = GlobalContext[DefaultProperty.DATA_DIR.key] ?: throw IllegalArgumentException()
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

    @Temporary(detail = "temp use json parse")
    private fun loadTableMeta(frmFile: File): Table {
        FileInputStream(frmFile).use { fileInputStream ->
            return fileInputStream.toObject<Table>()
        }
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
