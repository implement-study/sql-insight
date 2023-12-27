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

package org.gongxuanzhang.sql.insight.core.object;

import org.gongxuanzhang.sql.insight.core.environment.SessionContext;

import java.io.File;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public interface Index {


    /**
     * before search init method
     **/
    void rndInit();

    /**
     * index id in table.
     * primary key index always 1.
     **/
    int getId();

    /**
     * which table index belong to
     **/
    Table belongTo();

    /**
     * find a cursor from session
     *
     * @return cursor
     **/
    Cursor find(SessionContext sessionContext);

    /**
     * index name
     **/
    String getName();

    /**
     * insert row to index
     *
     * @param row row
     **/
    void insert(InsertRow row);

    /**
     * Index file
     **/
    File getFile();

    /**
     * index relative columns
     **/
    List<Column> columns();
}
