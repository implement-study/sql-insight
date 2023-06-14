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

package org.gongxuanzhang.mysql.service.executor.session.show;

import org.gongxuanzhang.mysql.annotation.DependOnContext;
import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.entity.Column;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.page.Compact;
import org.gongxuanzhang.mysql.entity.page.CompactNullValue;
import org.gongxuanzhang.mysql.entity.page.InnoDbPage;
import org.gongxuanzhang.mysql.entity.page.Supremum;
import org.gongxuanzhang.mysql.entity.page.UserRecord;
import org.gongxuanzhang.mysql.entity.page.Variables;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static org.gongxuanzhang.mysql.tool.PageUtils.getUserRecordByOffset;

/**
 * 展示一个页
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@DependOnContext
public class PageShower {

    private final InnoDbPage page;

    public PageShower(InnoDbPage page) {
        this.page = page;
    }

    public String pageString() {
        TableInfo tableInfo = this.page.getTableInfo();
        List<List<String>> tableShow = new ArrayList<>();
        List<Integer> maxLength = new ArrayList<>();
        for (Column column : tableInfo.getColumns()) {
            maxLength.add(column.getName().length());
        }
        short offset = (short) ConstantSize.INFIMUM.offset();
        while (true) {
            UserRecord userRecord = getUserRecordByOffset(page, offset);
            if (userRecord instanceof Supremum) {
                break;
            }
            offset = (short) userRecord.getRecordHeader().getNextRecordOffset();
            if (userRecord instanceof Compact) {
                tableShow.add(rowContext((Compact) userRecord, tableInfo, maxLength));
            }
        }
        tableShow.add(0, tableInfo.getColumns().stream().map(Column::getName).collect(Collectors.toList()));
        return join(tableShow, maxLength);
    }

    private List<String> rowContext(Compact userRecord, TableInfo tableInfo, List<Integer> maxLength) {
        List<String> context = new ArrayList<>();
        List<Column> columns = tableInfo.getColumns();
        CompactNullValue nullValues = userRecord.getNullValues();
        Variables variables = userRecord.getVariables();
        ByteBuffer body = ByteBuffer.wrap(userRecord.getBody());
        int variableIndex = 0;
        for (int i = 0; i < columns.size(); i++) {
            Column currentCol = columns.get(i);
            if (!currentCol.isNotNull() && nullValues.isNull(currentCol.getNullIndex())) {
                context.add(null);
                maxLength.set(i, Math.max(maxLength.get(i), 4));
                continue;
            }
            if (currentCol.isDynamic()) {
                byte length = variables.get(variableIndex);
                byte[] buffer = new byte[length];
                body.get(buffer);
                String value = new String(buffer);
                context.add(value);
                variableIndex++;
                maxLength.set(i, Math.max(maxLength.get(i), value.length()));
            } else {
                context.add(String.valueOf(body.getInt()));
            }
        }
        return context;
    }


    private String join(List<List<String>> tableList, List<Integer> maxLength) {
        String breakStr = breakStr(maxLength);
        StringJoiner totalJoiner = new StringJoiner("\r\n", breakStr, breakStr);
        for (List<String> row : tableList) {
            StringJoiner rowJoiner = new StringJoiner("|", "|", "|");
            for (int i = 0; i < row.size(); i++) {
                String value = row.get(i);
                StringBuilder cell = new StringBuilder(value);
                for (int i1 = 0; i1 < (maxLength.get(i) - cell.length()); i1++) {
                    cell.append(" ");
                }
                rowJoiner.add(cell.toString());
            }
            totalJoiner.add(rowJoiner.toString());
        }
        return totalJoiner.toString();
    }

    private String breakStr(List<Integer> maxLength) {
        StringJoiner stringJoiner = new StringJoiner("+", "+", "+");
        for (Integer integer : maxLength) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < integer; i++) {
                stringBuilder.append("-");
            }
            stringJoiner.add(stringBuilder.toString());
        }
        return "\r\n" + stringJoiner + "\r\n";
    }

}
