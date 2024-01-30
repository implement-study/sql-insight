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

import java.io.FileNotFoundException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * The top-level interface of the context
 *
 * @author gongxuanzhangmelt@gmail.com
 */
interface Context {
    /**
     * put a pair in context
     *
     * @param key   key unique
     * @param value value
     */
    operator fun set(key: String, value: String)

    /**
     * get value from context
     *
     * @param key pair of key
     * @return maybe null
     */
    operator fun get(key: String): String?

    /**
     * Method Overloading [Context.get]
     * @return not null
     */
    operator fun get(property: DefaultProperty): String

    /**
     * remove pair from context
     *
     * @param key
     * @return return the removed value if exists , else return null
     */
    fun remove(key: String): String?
}


/**
 * delegate a map implement [Context]
 */
abstract class AbstractMapContext(private val container: MutableMap<String, String> = ConcurrentHashMap()) : Context {
    override fun set(key: String, value: String) {
        container[key] = value
    }

    override fun get(key: String): String? {
        return container[key]
    }


    override fun remove(key: String): String? {
        return container.remove(key)
    }

    override fun get(property: DefaultProperty): String? {
        return get(property.key)
    }
}

object GlobalContext : AbstractMapContext() {
    init {
        DefaultProperty.entries.forEach {
            this[it.key] = it.value
        }

        val configFileName = System.getProperty("default-file", "/mysql.properties")
        val inputStream = GlobalContext::class.java.getResourceAsStream(configFileName)
            ?: throw FileNotFoundException("check your default-file property $configFileName")
        val userProperties = Properties().also { it.load(inputStream) }
        userProperties.forEach {
            this[it.key.toString()] = it.value.toString()
        }
    }
}






