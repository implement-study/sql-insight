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

package org.gongxuanzhang.mysql.core.manager;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.annotation.InitAfter;
import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.MySQLInitException;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.gongxuanzhang.mysql.entity.TableInfo.GFRM_SUFFIX;

/**
 * 表管理
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
@InitAfter(EngineManager.class)
public class TableManager extends AbstractManager<TableInfo> {


    private final DatabaseManager databaseManager;

    private final Map<Integer, TableInfo> spaceIdCache;

    private final AtomicInteger spaceId = new AtomicInteger();

    public TableManager(DatabaseManager databaseManager) throws MySQLException {
        this.databaseManager = databaseManager;
        this.spaceIdCache = new ConcurrentHashMap<>();
    }

    /**
     * 表空间查询
     *
     * @param spaceId 表空间
     * @return 表信息
     **/
    public TableInfo select(int spaceId) {
        return spaceIdCache.get(spaceId);
    }


    @Override
    public void register(TableInfo tableInfo) {
        super.register(tableInfo);
        this.spaceId.set(Integer.max(spaceId.get(), tableInfo.getSpaceId()));
        spaceIdCache.put(tableInfo.getSpaceId(), tableInfo);
    }


    /**
     * 拿到下一个spaceId
     *
     * @return 表空间id
     **/
    public int getNextSpaceId() {
        return spaceId.incrementAndGet();
    }

    @Override
    protected String errorMessage() {
        return "表";
    }

    @Override
    protected void init() throws MySQLException {
        databaseManager.getAll().stream()
                .map(DatabaseInfo::sourceFile)
                .map(dbDir -> dbDir.listFiles((file) -> file.getName().endsWith(GFRM_SUFFIX)))
                .filter(Objects::nonNull)
                .flatMap(Stream::of)
                .map(this::gfrmToInfo)
                .forEach(this::register);
    }

    public void removeDatabase(String database) {
        this.getCache().keySet().stream()
                .filter(tableName -> tableName.startsWith(database))
                .collect(Collectors.toList())
                .forEach(this.getCache()::remove);

    }


    private TableInfo gfrmToInfo(File gfrmFile) {
        try (FileInputStream fileInputStream = new FileInputStream(gfrmFile);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return (TableInfo) objectInputStream.readObject();
        } catch (Exception e) {
            throw new MySQLInitException(gfrmFile.getName() + "表文件有问题，无法启动mysql,错误信息:" + e.getMessage());
        }

    }

    @Override
    public String toName(TableInfo info) {
        return info.absoluteName();
    }
}
