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

package org.gongxuanzhang.sql.insight.core.engine.json;

import org.gongxuanzhang.sql.insight.core.environment.SessionContext;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.object.Column;
import org.gongxuanzhang.sql.insight.core.object.Cursor;
import org.gongxuanzhang.sql.insight.core.object.Index;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.Table;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class JsonPkIndex implements Index {

    private final Table table;

    private Path jsonFilePath;


    protected JsonPkIndex(Table table) {
        this.table = table;

    }


    @Override
    public void rndInit() {
        this.jsonFilePath = JsonEngineSupport.getJsonFile(table).toPath();
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public Table belongTo() {
        return this.table;
    }

    @Override
    public Cursor find(SessionContext sessionContext) {
        try {
            BufferedReader reader = Files.newBufferedReader(jsonFilePath);
            return new JsonCursor(reader, sessionContext, this);
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }

    @Override
    public String getName() {
        return "json";
    }

    @Override
    public void insert(InsertRow row) {
        throw new UnsupportedOperationException("json engine index dont support insert");
    }

    @Override
    public File getFile() {
        return this.jsonFilePath.toFile();
    }

    @Override
    public List<Column> columns() {
        throw new UnsupportedOperationException("json don't support");
    }


}
