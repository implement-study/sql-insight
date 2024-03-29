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
package tech.insight.core.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import tech.insight.core.environment.DefaultProperty
import tech.insight.core.environment.GlobalContext
import java.io.File

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class Database(val name: String) {

    val dbFolder: File
        @JsonIgnore
        get() {
            return File(GlobalContext[DefaultProperty.DATA_DIR], name)
        }

    override fun toString(): String {
        return "Database[$name]"
    }
}
