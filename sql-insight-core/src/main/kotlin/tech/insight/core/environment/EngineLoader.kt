/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/sql-insight/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.insight.core.environment

import lombok.extern.slf4j.Slf4j
import java.lang.reflect.InvocationTargetException
import java.net.URL
import java.util.*

/**
 * if you want register your custom engine.
 * add your engine absolute class name in META-INF/engine.properties
 * engine=classname1,classname2,
 *
 * @author gongxuanzhangmelt@gmail.com
 */
@Slf4j
object EngineLoader {
    private const val ENGINE_RESOURCE_LOCATION = "META-INF/engine.properties"
    fun loadEngine(): List<StorageEngine?> {
        val engineList: MutableList<StorageEngine?> = ArrayList<StorageEngine?>()
        try {
            val urls: Enumeration<URL> = EngineLoader::class.java.getClassLoader().getResources(
                ENGINE_RESOURCE_LOCATION
            )
            while (urls.hasMoreElements()) {
                val engine = Properties()
                val url: URL = urls.nextElement()
                val resource = UrlResource(url)
                resource.getInputStream().use { inputStream -> engine.load(inputStream) }
                val engineNames = engine.getProperty("engine")
                val engineNameArray = engineNames.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (name in engineNameArray) {
                    engineList.add(reflectEngine(name))
                }
            }
        } catch (e: IOException) {
            log.error("load engine error", e)
        }
        return engineList
    }

    private fun reflectEngine(name: String): StorageEngine {
        return try {
            val engineClass = Class.forName(name)
            val constructor = engineClass.getConstructor()
            constructor.newInstance() as StorageEngine
        } catch (e: ClassNotFoundException) {
            throw EngineLoadException(e, name)
        } catch (e: NoSuchMethodException) {
            throw EngineLoadException(e, name)
        } catch (e: InvocationTargetException) {
            throw EngineLoadException(e)
        } catch (e: InstantiationException) {
            throw EngineLoadException(e)
        } catch (e: IllegalAccessException) {
            throw EngineLoadException(e)
        }
    }
}
