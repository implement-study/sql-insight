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

package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.LambdaExpectionRuntimeWrapper;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.tool.ExceptionThrower;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InnoDbPageSelector implements PageSelector {

    private final static Map<String, InnoDbPageSelector> INSTANCE_CACHE = new ConcurrentHashMap<>();

    private final TableInfo tableInfo;

    private final RandomAccessFile pageFile;

    private InnoDbPageSelector(TableInfo tableInfo) throws MySQLException {
        this.tableInfo = tableInfo;
        try {
            this.pageFile = new RandomAccessFile(tableInfo.dataFile(), "rw");
        } catch (FileNotFoundException e) {
            throw new MySQLException(e);
        }
    }

    public static InnoDbPageSelector open(TableInfo tableInfo) throws MySQLException {
        String tableName = tableInfo.getTableName();
        try {
            return INSTANCE_CACHE.computeIfAbsent(tableName, k -> {
                try {
                    return new InnoDbPageSelector(tableInfo);
                } catch (MySQLException e) {
                    throw new LambdaExpectionRuntimeWrapper(e);
                }
            });
        } catch (LambdaExpectionRuntimeWrapper e) {
            e.wrapMySQLException();
        }
        return null;
    }

    @Override
    public byte[] getRootPage() throws MySQLException {
        byte[] rootPage = ConstantSize.PAGE.emptyBuff();
        try {
            if (pageFile.read(rootPage) != rootPage.length) {
                throw new MySQLException("根页读取错误");
            }
        } catch (IOException e) {
            ExceptionThrower.errorSwap(e);
        }
        return rootPage;
    }
}
