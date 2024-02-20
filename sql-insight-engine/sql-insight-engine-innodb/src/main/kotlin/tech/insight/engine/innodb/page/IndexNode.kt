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
package tech.insight.engine.innodb.page

import org.gongxuanzhang.easybyte.core.ByteWrapper
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer
import tech.insight.core.bean.value.Value


/**
 * index page contains some index node.
 * a index node contains a pointer and index key.
 *
 * @author gongxuanzhangmelt@gmail.com
 */
class IndexNode(key: Array<Value<*>>, pointer: Int) : PageObject, ByteWrapper {
    private val length: Int
    private val key: Array<Value<*>>
    private val pointer: Int

    init {
        this.key = key
        var candidate = 0
        for (value in key) {
            candidate += value.length
        }
        length = candidate
        this.pointer = pointer
    }

    override fun length(): Int {
        return key.size + Integer.BYTES
    }

    override fun toBytes(): ByteArray {
        val buffer: DynamicByteBuffer = DynamicByteBuffer.allocate()
        for (value in key) {
            buffer.append(value.toBytes())
        }
        buffer.appendInt(pointer)
        return buffer.toBytes()
    }
}
