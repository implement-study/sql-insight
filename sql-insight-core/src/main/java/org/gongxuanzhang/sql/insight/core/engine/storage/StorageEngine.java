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

package org.gongxuanzhang.sql.insight.core.engine.storage;


import org.gongxuanzhang.sql.insight.core.command.dml.Update;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.Row;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.result.ResultInterface;

import java.util.List;

/**
 * storage engine , executor select the engine
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public interface StorageEngine {


    /**
     * engine name,sole
     *
     * @return engine name, may be throw
     * {@link org.gongxuanzhang.sql.insight.core.exception.DuplicationEngineNameException}
     **/
    String getName();

    /**
     * the file extension that  storage engine create table
     *
     * @return may be a empty list
     * @see <a href="https://www.mysqlzh.com/doc/147.html"/>
     **/
    List<String> tableExtensions();


    /**
     * open the table and index
     **/
    void openTable(Table table);

    /**
     * create table
     **/
    ResultInterface createTable(Table table);

    /**
     * truncate table,reserved table construction
     **/
    ResultInterface truncateTable(Table table);

    /**
     * insert data
     *
     * @param row insert
     **/
    void insertRow(InsertRow row);

    /**
     * update
     *
     * @param oldRow hit update condition
     * @param update update set info
     **/
    void update(Row oldRow, Update update);

    /**
     * delete
     *
     * @param deletedRow hit delete condition
     **/
    void delete(Row deletedRow);

    /**
     * refresh the data
     **/
    void refresh(Table table);


}
