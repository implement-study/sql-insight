/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.entity.Cell;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.dml.InsertExecutor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;
import org.gongxuanzhang.mysql.service.token.TokenSupport;
import org.gongxuanzhang.mysql.storage.StorageEngine;
import org.gongxuanzhang.mysql.tool.Context;
import org.gongxuanzhang.mysql.tool.ExceptionThrower;

import java.util.ArrayList;
import java.util.List;

/**
 * insert 解析器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InsertAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws MySQLException {
        TokenSupport.mustTokenKind(sqlTokenList.get(1), TokenKind.INTO);
        InsertInfo info = new InsertInfo();
        int offset = 2;
        offset += TokenSupport.fillTableInfo(info, sqlTokenList, offset);
        final int finalOffset = offset;
        TokenSupport.token(sqlTokenList.get(finalOffset))
                .when(TokenKind.LEFT_PAREN)
                .then(() -> haveColumns(info, sqlTokenList, finalOffset))
                .when(TokenKind.VALUES)
                .then(() -> withoutColumns(info, sqlTokenList, finalOffset))
                .get();
        StorageEngine engine = Context.selectStorageEngine(info.getTableInfo());
        return new InsertExecutor(engine, info);
    }


    private void haveColumns(InsertInfo info, List<SqlToken> sqlTokenList, int offset) throws MySQLException {
        TokenSupport.mustTokenKind(sqlTokenList.get(offset), TokenKind.LEFT_PAREN);
        offset++;
        while (!TokenSupport.isTokenKind(sqlTokenList.get(offset), TokenKind.RIGHT_PAREN)) {
            switch (sqlTokenList.get(offset).getTokenKind()) {
                case COMMA:
                    offset++;
                    break;
                case VAR:
                    info.getColumns().add(sqlTokenList.get(offset).getValue());
                    offset++;
                    break;
                default:
                    ExceptionThrower.errorToken(sqlTokenList.get(offset));
            }
        }
        offset++;
        withoutColumns(info, sqlTokenList, offset);
    }

    private void withoutColumns(InsertInfo info, List<SqlToken> sqlTokenList, int offset) throws MySQLException {
        TokenSupport.mustTokenKind(sqlTokenList.get(offset), TokenKind.VALUES);
        int subOffset = 0;
        do {
            subOffset += addValue(info, sqlTokenList, offset + subOffset + 1);
        }
        while (subOffset + offset + 1 < sqlTokenList.size());
    }

    /**
     * 添加一行记录，返回偏移量
     *
     * @return 偏移量
     **/
    private int addValue(InsertInfo info, List<SqlToken> sqlTokenList, int offset) throws MySQLException {
        List<Cell<?>> row = new ArrayList<>();
        TokenSupport.mustTokenKind(sqlTokenList.get(offset), TokenKind.LEFT_PAREN);
        int currentOffset = 1;
        while (!TokenSupport.isTokenKind(sqlTokenList.get(offset + currentOffset), TokenKind.RIGHT_PAREN)) {
            if (currentOffset % 2 == 1) {
                Cell<?> cell = TokenSupport.parseCell(sqlTokenList.get(offset + currentOffset));
                row.add(cell);
            } else {
                TokenSupport.mustTokenKind(sqlTokenList.get(offset + currentOffset), TokenKind.COMMA);
            }
            currentOffset++;
        }
        if (info.getInsertData() == null) {
            info.setInsertData(new ArrayList<>());
        }
        info.getInsertData().add(row);
        // 最后偏移 右括号
        currentOffset++;
        //  如果还没有结束 判断下一个token是不是 ","
        if (currentOffset + offset + 1 < sqlTokenList.size()) {
            TokenSupport.mustTokenKind(sqlTokenList.get(offset + currentOffset), TokenKind.COMMA);
            currentOffset++;
        }
        return currentOffset;
    }

}
