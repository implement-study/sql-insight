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

package org.gongxuanzhang.sql.insight.core.event

import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger


class EventPublisherTest {

    @Test
    fun testRegister() {
        val publisher = EventPublisher.getInstance()
        val counter1 = AtomicInteger()
        val counter2 = AtomicInteger()
        val counter3 = AtomicInteger()
        publisher.registerListener(TestEvent1Listener(counter1))
        publisher.registerListener(TestEvent2Listener(counter2))
        publisher.registerListener(TestMultiEventListener(counter1, counter2, counter3))
        publisher.publishEvent(TestEvent1(""))
        publisher.publishEvent(TestEvent2(""))
        publisher.publishEvent(TestEvent3(""))
        assert(counter1.get() == 2)
        assert(counter2.get() == 2)
        assert(counter3.get() == 1)
    }
}


class TestEvent1Listener(private val counter: AtomicInteger) : EventListener<TestEvent1> {

    override fun onEvent(event: TestEvent1) {
        counter.incrementAndGet()
    }

}

class TestEvent2Listener(private val counter: AtomicInteger) : EventListener<TestEvent2> {

    override fun onEvent(event: TestEvent2) {
        counter.incrementAndGet()
    }

}

class TestMultiEventListener(
    private val counter1: AtomicInteger,
    private val counter2: AtomicInteger,
    private val counter3: AtomicInteger
) : MultipleEventListener {


    override fun onEvent(event: InsightEvent) {
        when (event) {
            is TestEvent1 -> {
                counter1.incrementAndGet()
            }

            is TestEvent2 -> {
                counter2.incrementAndGet()
            }

            else -> {
                counter3.incrementAndGet()
            }
        }
    }

    override fun listenEvent(): MutableList<Class<out InsightEvent>> {
        return mutableListOf(TestEvent1::class.java, TestEvent2::class.java, TestEvent3::class.java)
    }

}

class TestEvent1(source: Any) : InsightEvent(source)

class TestEvent2(source: Any) : InsightEvent(source)

class TestEvent3(source: Any) : InsightEvent(source)

