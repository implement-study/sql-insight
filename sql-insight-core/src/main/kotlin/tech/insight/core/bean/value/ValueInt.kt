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
package tech.insight.core.bean.value

import lombok.EqualsAndHashCode

/**
 * @author gongxuanzhangmelt@gmail.com
 */
@EqualsAndHashCode
class ValueInt : BaseValue {
    private val value: Int

    constructor(value: Int) {
        this.value = value
    }

    constructor(value: Value) {
        try {
            this.value = value.getSource().toString().toInt()
        } catch (e: Exception) {
            throw DateTypeCastException("int", value.javaClass.getName())
        }
    }

    override val source: Any?
        get() = value
    override val length: Int
        get() = Integer.BYTES

    override fun toBytes(): ByteArray {
        return ByteArrays.fromInt(value)
    }

    override fun compareTo(o: Value): Int {
        if (o is ValueInt) {
            return Integer.compare(value, o.value)
        }
        throw IllegalArgumentException("ValueInt can't compare to " + o.javaClass.getName())
    }
}
