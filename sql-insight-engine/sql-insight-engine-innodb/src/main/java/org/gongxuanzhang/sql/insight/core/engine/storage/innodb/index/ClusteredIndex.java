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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.index;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.sql.insight.core.engine.AutoIncrementKeyCounter;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.core.InnodbIc;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.RootPage;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.Compact;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RowFormatFactory;
import org.gongxuanzhang.sql.insight.core.environment.SessionContext;
import org.gongxuanzhang.sql.insight.core.object.Cursor;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.Table;


/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
public class ClusteredIndex extends InnodbIndex {

    private AutoIncrementKeyCounter autoIncrementKeyCounter;

    protected ClusteredIndex(Table table) {
        super(table);
    }


    @Override
    public void rndInit() {
        if (this.table.getExt().getAutoColIndex() >= 0) {
            this.autoIncrementKeyCounter = new InnodbIc(table);
        }
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public Cursor find(SessionContext sessionContext) {
        return null;
    }

    @Override
    public void insert(InsertRow row) {
        if (this.autoIncrementKeyCounter.dealAutoIncrement(row)) {
            log.info("auto increment primary key {}",
                    table.getColumnList().get(table.getExt().getAutoColIndex()).getName());
        }
        Compact compact = RowFormatFactory.compactFromInsertRow(row);
        RootPage root = getRoot();
        root.insertData(compact);
    }


}
