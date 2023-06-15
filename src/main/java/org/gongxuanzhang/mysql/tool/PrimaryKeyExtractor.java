/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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

package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.entity.CompositePrimaryKey;
import org.gongxuanzhang.mysql.entity.InsertRow;
import org.gongxuanzhang.mysql.entity.PrimaryKey;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * 辅助抽取主键
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class PrimaryKeyExtractor {


    private PrimaryKeyExtractor() {

    }


    public static PrimaryKey extract(InsertRow insertRow) throws MySQLException {
        int[] primaryKeyIndex = insertRow.getTableInfo().getPrimaryKeyIndex();
        if (primaryKeyIndex.length == 0) {
            throw new UnsupportedOperationException("暂不支持无主键");
        }
        if (primaryKeyIndex.length == 1) {
            return insertRow.getCellList().get(primaryKeyIndex[0]).toPrimaryKey();
        }
        List<PrimaryKey> list = new ArrayList<>(primaryKeyIndex.length);
        for (int keyIndex : primaryKeyIndex) {
            list.add(insertRow.getCellList().get(keyIndex).toPrimaryKey());
        }
        return new CompositePrimaryKey(list);
    }

}
