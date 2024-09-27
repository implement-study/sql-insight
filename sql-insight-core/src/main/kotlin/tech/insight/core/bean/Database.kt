/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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
import java.io.File
import tech.insight.buffer.SerializableObject
import tech.insight.core.environment.DefaultProperty
import tech.insight.core.environment.GlobalContext

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class Database(val name: String) : SQLBean, SerializableObject {

    val dbFolder: File
        @JsonIgnore
        get() {
            return File(GlobalContext[DefaultProperty.DATA_DIR], name)
        }


    override fun toString(): String {
        return "Database[$name]"
    }

    override fun toBytes(): ByteArray {
        return name.toByteArray()
    }

    override fun parent(): SQLBean? {
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Database) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
