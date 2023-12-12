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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.core;

import org.gongxuanzhang.sql.insight.core.engine.AutoIncrementKeyCounter;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.value.Value;
import org.gongxuanzhang.sql.insight.core.object.value.ValueInt;
import org.gongxuanzhang.sql.insight.core.object.value.ValueNull;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class InnodbIc implements AutoIncrementKeyCounter {

    private Table table;

    private final int incrementColIndex;

    private final AtomicLong counter;

    public InnodbIc(Table table) {
        this.table = table;
        this.incrementColIndex = table.getAutoColIndex();
        //  todo
        this.counter = new AtomicLong(0);
    }

    @Override
    public boolean dealAutoIncrement(InsertRow row) {
        Value targetValue = row.getAbsoluteValueList().get(incrementColIndex);
        if (targetValue instanceof ValueNull) {
            row.getAbsoluteValueList().set(incrementColIndex, new ValueInt((int) this.counter.incrementAndGet()));
            return true;
        }
        Integer source = ((ValueInt) targetValue).getSource();
        if (source > this.counter.get()) {
            this.counter.set(source);
        }
        return false;
    }

    @Override
    public void reset(Table table) {
        this.counter.set(0);
    }
}
