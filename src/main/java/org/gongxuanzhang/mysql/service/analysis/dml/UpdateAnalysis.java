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

import org.gongxuanzhang.mysql.entity.SetInfo;
import org.gongxuanzhang.mysql.entity.UpdateInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.analysis.TokenAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.dml.UpdateExecutor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;
import org.gongxuanzhang.mysql.service.token.TokenSupport;
import org.gongxuanzhang.mysql.storage.StorageEngine;
import org.gongxuanzhang.mysql.tool.Context;
import org.gongxuanzhang.mysql.tool.ExceptionThrower;

import java.util.List;

import static org.gongxuanzhang.mysql.service.token.TokenSupport.isTokenKind;

/**
 * update 解析器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class UpdateAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws MySQLException {
        UpdateInfo updateInfo = new UpdateInfo();
        int offset = 1;
        offset += TokenSupport.fillTableInfo(updateInfo, sqlTokenList, 1);
        TokenSupport.mustTokenKind(sqlTokenList.get(offset), TokenKind.SET);
        offset++;
        offset += fillSet(updateInfo, sqlTokenList, offset);
        offset += TokenSupport.fillWhere(updateInfo, sqlTokenList.subList(offset, sqlTokenList.size()));
        ExceptionThrower.ifNotThrow(offset == sqlTokenList.size());
        StorageEngine engine = Context.selectStorageEngine(updateInfo.getTableInfo());
        return new UpdateExecutor(engine, updateInfo);
    }

    private int fillSet(UpdateInfo updateInfo, List<SqlToken> tokenList, int offset) throws SqlAnalysisException {
        SetInfo set = updateInfo.getSet();
        int newOffset = 0;
        while (offset + newOffset < tokenList.size()) {
            String colName = TokenSupport.tryGetString(tokenList.get(offset + newOffset));
            if (colName == null) {
                return newOffset;
            }
            newOffset++;
            TokenSupport.mustTokenKind(tokenList.get(offset + newOffset), TokenKind.EQUALS);
            newOffset++;
            if (isTokenKind(tokenList.get(offset + newOffset), TokenKind.LITERACY, TokenKind.VAR, TokenKind.NUMBER)) {
                String value = tokenList.get(offset + newOffset).getValue();
                set.addSet(colName, value);
            } else {
                String message = String.format("解析不出%s的值", colName);
                throw new SqlAnalysisException(message);
            }
            newOffset++;
            if (offset + newOffset == tokenList.size()) {
                break;
            }
            if (!isTokenKind(tokenList.get(offset + newOffset), TokenKind.COMMA)) {
                break;
            }
            newOffset++;
        }
        return newOffset;
    }


}
