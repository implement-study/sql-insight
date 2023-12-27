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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;

import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.index.InnodbIndex;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.IndexRecord;
import org.gongxuanzhang.sql.insight.core.object.value.Value;

import java.util.Comparator;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class IndexPage extends InnoDbPage implements Comparator<IndexRecord> {

    public IndexPage(InnodbIndex index) {
        super(index);
    }

    @Override
    public void insertData(InnodbUserRecord data) {

    }

    @Override
    protected InnodbUserRecord wrapUserRecord(int offsetInPage) {
        return null;
    }

    @Override
    protected void splitIfNecessary() {

    }

    @Override
    public int compare(IndexRecord o1, IndexRecord o2) {
        Value[] values1 = o1.indexNode().getKey();
        Value[] values2 = o2.indexNode().getKey();
        for (int i = 0; i < values1.length; i++) {
            int compare = values1[i].compareTo(values2[i]);
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
    }
}
