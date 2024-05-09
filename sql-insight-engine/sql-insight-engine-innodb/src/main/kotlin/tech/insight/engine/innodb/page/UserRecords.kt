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
package tech.insight.engine.innodb.page

import org.gongxuanzhang.easybyte.core.ByteWrapper
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class UserRecords(var body: ByteArray = ByteArray(0)) : ByteWrapper, PageObject {

    override fun toBytes(): ByteArray {
        return body
    }

    fun addRecord(userRecord: InnodbUserRecord) {
        body = DynamicByteBuffer.wrap(body).append(userRecord.toBytes()).toBytes()
    }

    fun addRecords(userRecord: List<InnodbUserRecord>) {
        body = DynamicByteBuffer.wrap(body).apply {
            userRecord.forEach { append(it.toBytes()) }
        }.toBytes()
    }

    override fun length(): Int {
        return body.size
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserRecords) return false

        if (!body.contentEquals(other.body)) return false

        return true
    }

    override fun hashCode(): Int {
        return body.contentHashCode()
    }


}
