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

import org.gongxuanzhang.sql.insight.core.exception.RuntimeFileNotFoundException
import java.util.*

/**
 * like my.cnf
 * default file path classpath:mysql.properties
 *
 * @author gongxuanzhangmelt@gmail.com
 */
class SqlInsightProperties : Properties() {
    init {
        val inputStream = javaClass.getResourceAsStream(CONFIG_FILE_NAME)
            ?: throw RuntimeFileNotFoundException()
        try {
            load(inputStream)
        } catch (e: IOException) {
            throw RuntimeIoException(e)
        }
    }

    companion object {
        private val CONFIG_FILE_NAME: String? = null

        init {
            CONFIG_FILE_NAME = System.getProperty("defaults-file", "/mysql.properties")
        }
    }
}
