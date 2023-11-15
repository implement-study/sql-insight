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

package org.gongxuanzhang.sql.insight.core.environment;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.sql.insight.core.annotation.Temporary;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.object.Table;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class TableLoader {


    public static List<Table> loadTable() {
        GlobalContext context = GlobalContext.getInstance();
        String home = context.get(DefaultProperty.DATA_DIR.getKey());
        if (home == null) {
            throw new IllegalArgumentException();
        }
        File[] dbArray = new File(home).listFiles(File::isDirectory);
        if (dbArray == null) {
            return Collections.emptyList();
        }
        List<Table> tableList = new ArrayList<>();
        for (File dbFile : dbArray) {
            File[] frmFileArray = dbFile.listFiles(f -> f.getName().endsWith(".frm"));
            if (frmFileArray == null) {
                continue;
            }
            for (File frmFile : frmFileArray) {
                tableList.add(load(frmFile));
            }
        }
        return tableList;
    }


    @Temporary(detail = "temp use json parse")
    private static Table load(File frmFile) {
        try (FileInputStream fileInputStream = new FileInputStream(frmFile)) {
            JSONObject jsonObject = JSON.parseObject(fileInputStream);
            return jsonObject.toJavaObject(Table.class);
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }

    }
}
