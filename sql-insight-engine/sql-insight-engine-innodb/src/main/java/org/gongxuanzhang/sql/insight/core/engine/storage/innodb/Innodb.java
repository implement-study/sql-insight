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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.sql.insight.core.command.dml.Update;
import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.PageFactory;
import org.gongxuanzhang.sql.insight.core.object.Index;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.Row;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.result.MessageResult;
import org.gongxuanzhang.sql.insight.core.result.ResultInterface;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
public class Innodb implements StorageEngine {

    @Override
    public String getName() {
        return "innodb";
    }

    @Override
    public List<String> tableExtensions() {
        return Collections.singletonList("ibd");
    }

    @Override
    public void openTable(Table table) {
        List<Index> indexList = table.getIndexList();

    }

    @Override
    public ResultInterface createTable(Table table) {
        PageFactory.initialization(table);
        log.info("create table {} with innodb,create ibd file", table.getName());
        return new MessageResult(String.format("成功创建%s", table.getName()));
    }

    @Override
    public ResultInterface truncateTable(Table table) {
        return null;
    }

    @Override
    public void insertRow(InsertRow row) {
        Table table = row.getTable();
        openTable(table);
        List<Index> indexList = table.getIndexList();
        for (Index index : indexList) {
            index.insert(row);
        }
    }

    @Override
    public void update(Row oldRow, Update update) {

    }

    @Override
    public void delete(Row deletedRow) {

    }

    @Override
    public void refresh(Table table) {

    }


}
