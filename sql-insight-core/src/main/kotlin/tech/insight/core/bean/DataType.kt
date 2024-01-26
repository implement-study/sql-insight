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

import com.alibaba.druid.sql.ast.SQLDataType
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType
import java.util.*

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class DataType : FillDataVisitor {
    var type: Type? = null
    var length = 0
    override fun endVisit(x: SQLDataType) {
        type = Type.valueOf(x.name.uppercase(Locale.getDefault()))
        length = type!!.defaultLength
    }

    override fun endVisit(x: SQLCharacterDataType) {
        type = Type.valueOf(x.name.uppercase(Locale.getDefault()))
        length = x.getLength()
        if (length < 0) {
            length = type!!.defaultLength
        }
    }

    enum class Type(val defaultLength: Int) {
        INT(4),
        VARCHAR(255),
        CHAR(255)
    }
}
