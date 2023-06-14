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

import org.gongxuanzhang.mysql.entity.Column;
import org.gongxuanzhang.mysql.entity.CompositePrimaryKey;
import org.gongxuanzhang.mysql.entity.InfimumPrimaryKey;
import org.gongxuanzhang.mysql.entity.InsertRow;
import org.gongxuanzhang.mysql.entity.IntegerPrimaryKey;
import org.gongxuanzhang.mysql.entity.PrimaryKey;
import org.gongxuanzhang.mysql.entity.SupremumPrimaryKey;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.VarcharPrimaryKey;
import org.gongxuanzhang.mysql.entity.page.Compact;
import org.gongxuanzhang.mysql.entity.page.Infimum;
import org.gongxuanzhang.mysql.entity.page.Supremum;
import org.gongxuanzhang.mysql.entity.page.UserRecord;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static PrimaryKey extract(UserRecord userRecord, TableInfo tableInfo) {
        if (userRecord instanceof Infimum) {
            return new InfimumPrimaryKey();
        }
        if (userRecord instanceof Supremum) {
            return new SupremumPrimaryKey();
        }
        if (userRecord instanceof Compact) {
            return compactPrimaryKeyExtract((Compact) userRecord, tableInfo);
        }
        throw new UnsupportedOperationException("暂不支持" + userRecord.getClass().getName() + "行格式");
    }

    private static PrimaryKey compactPrimaryKeyExtract(Compact compact, TableInfo tableInfo) {
        int[] primaryKeyIndex = tableInfo.getPrimaryKeyIndex();
        if (primaryKeyIndex.length == 0) {
            throw new UnsupportedOperationException("暂不支持无主键");
        }
        if (primaryKeyIndex.length == 1) {
            int primaryLength;
            Column primaryCol = tableInfo.getColumns().get(0);
            if (primaryCol.isDynamic()) {
                primaryLength = compact.getVariables().get(0);
            } else {
                primaryLength = primaryCol.getLength();
            }
            byte[] primaryBody = Arrays.copyOfRange(compact.getBody(), 0, primaryLength);
            switch (primaryCol.getType()) {
                case INT:
                    return new IntegerPrimaryKey(BitUtils.joinInt(primaryBody));
                case VARCHAR:
                    return new VarcharPrimaryKey(new String(primaryBody));
                default:
                    throw new UnsupportedOperationException("不支持" + primaryCol.getType() + "类型主键");
            }
        } else {
            throw new UnsupportedOperationException("暂不支持联合主键");
        }
    }

    //  找到目标位置的


}
