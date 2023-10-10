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

package org.gongxuanzhang.sql.insight.core.engine.storage

import org.gongxuanzhang.sql.insight.core.exception.DuplicationEngineNameException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class SimpleStorageEngineManagerTest {
    @Test
    fun testRegister() {
        val manager = SimpleStorageEngineManager()
        val testEngine = object : AbstractStorageEngine() {
            override fun getName(): String {
                return "test"
            }
        }
        val testEngine1 = object : AbstractStorageEngine() {
            override fun getName(): String {
                return "test1"
            }
        }
        manager.registerEngine(testEngine)
        manager.registerEngine(testEngine1)
        assert(manager.selectEngine(testEngine.name) == testEngine)
        assert(manager.selectEngine(testEngine1.name) == testEngine1)
        assert(manager.allEngine() == listOf(testEngine, testEngine1))
    }

    @Test
    fun testDuplicateRegister() {
        val manager = SimpleStorageEngineManager()
        val testEngine = object : AbstractStorageEngine() {
            override fun getName(): String {
                return "test"
            }
        }
        manager.registerEngine(testEngine)
        assertThrows<DuplicationEngineNameException> { manager.registerEngine(testEngine) }
    }
}

