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

import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.sql.insight.core.environment.SessionContext;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.object.Cursor;
import org.gongxuanzhang.sql.insight.core.object.Row;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class JsonCursor implements Cursor {

    private final BufferedReader reader;

    private final JsonPkIndex index;

    private Row current;

    private final SessionContext currentSession;

    public JsonCursor(BufferedReader reader, SessionContext currentSession, JsonPkIndex index) {
        this.reader = reader;
        this.currentSession = currentSession;
        this.index = index;
    }


    @Override
    public boolean hasNext() {
        if (current != null) {
            return true;
        }
        try {
            String line = reader.readLine();
            if (line == null) {
                return false;
            }
            this.current = JsonEngineSupport.getPhysicRowFromJson(JSONObject.parseObject(line), index.getTable());
            return true;
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }

    @Override
    public Row next() {
        if (current == null) {
            throw new NoSuchElementException();
        }
        return current;
    }

    @Override
    public void close() {
        if (this.reader != null) {
            try {
                this.reader.close();
            } catch (IOException e) {
                throw new RuntimeIoException(e);
            }
        }
    }


}
