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

package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.annotation.InitAfter;
import org.gongxuanzhang.mysql.core.EngineSelectable;
import org.gongxuanzhang.mysql.core.MySqlProperties;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.core.manager.DatabaseManager;
import org.gongxuanzhang.mysql.core.manager.EngineManager;
import org.gongxuanzhang.mysql.core.manager.MySQLManager;
import org.gongxuanzhang.mysql.core.manager.TableManager;
import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.entity.GlobalProperties;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.MySQLInitException;
import org.gongxuanzhang.mysql.storage.StorageEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.gongxuanzhang.mysql.core.MySqlProperties.DEFAULT_STORAGE_ENGINE;

/**
 * 环境辅助类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class Context {


    private final static DatabaseManager DATABASE_MANAGER;
    private final static TableManager TABLE_MANAGER;
    private final static EngineManager ENGINE_MANAGER;

    static {
        try {
            DATABASE_MANAGER = new DatabaseManager();
            ENGINE_MANAGER = new EngineManager();
            TABLE_MANAGER = new TableManager(DATABASE_MANAGER);
            ManagerInitialization init = new ManagerInitialization();
            init.registerManager(TABLE_MANAGER);
            init.registerManager(ENGINE_MANAGER);
            init.registerManager(DATABASE_MANAGER);
            init.managerRefresh();
        } catch (MySQLException e) {
            throw new RuntimeException(e);
        }
    }


    private Context() {

    }

    public static TableManager getTableManager() {
        return TABLE_MANAGER;
    }

    public static DatabaseManager getDatabaseManager() {
        return DATABASE_MANAGER;
    }

    public static EngineManager getEngineManager() {
        return ENGINE_MANAGER;
    }


    /**
     * 向引擎管理器注册引擎
     **/
    public static void registerEngine(StorageEngine engine) {
        ENGINE_MANAGER.register(engine);
    }

    /**
     * 通过引擎名获取引擎
     **/
    public static StorageEngine selectStorageEngine(String engineName) throws MySQLException {
        if (engineName == null) {
            engineName = GlobalProperties.instance.get(DEFAULT_STORAGE_ENGINE);
        }
        return ENGINE_MANAGER.select(engineName);
    }


    /**
     * 把一个简单的tokenInfo 填充满
     *
     * @return 一个有完全表信息的table Info
     **/
    public static TableInfo fillTableInfo(TableInfo info) throws MySQLException {
        if (info.getDatabase() == null) {
            DatabaseInfo database = SessionManager.currentSession().getDatabase();
            info.setDatabase(database);
        }
        String key = info.absoluteName();
        TableInfo select = TABLE_MANAGER.select(key);
        if (select == null) {
            throw new MySQLException(key + "表不存在");
        }
        return select;
    }


    /**
     * 通过选择器拿到目标引擎
     **/
    public static StorageEngine selectStorageEngine(EngineSelectable engineSelectable) throws MySQLException {
        return selectStorageEngine(engineSelectable.getEngineName());
    }

    /**
     * 拿到所有引擎信息
     **/
    public static List<StorageEngine> getEngineList() {
        return ENGINE_MANAGER.getAll();
    }


    /**
     * 获得数据库根目录
     **/
    public static File getHome() {
        GlobalProperties properties = GlobalProperties.getInstance();
        String dataDir = properties.get(MySqlProperties.DATA_DIR);
        return new File(dataDir);
    }

    /**
     * 拿到数据库目录
     *
     * @param database 数据库名
     * @return 数据库目录
     * @throws MySQLException 数据库不存在或者文件异常会抛出异常
     **/
    public static File getDatabaseHome(String database) throws MySQLException {
        File file = new File(getHome(), database);
        if (!file.exists() || !file.isDirectory()) {
            throw new MySQLException("数据库[" + database + "]有问题");
        }
        return file;
    }

    /**
     * 拿到数据库目录
     *
     * @param database 数据库实体
     * @return 数据库目录
     * @throws MySQLException 数据库不存在或者文件异常会抛出异常
     **/
    public static File getDatabaseHome(DatabaseInfo database) throws MySQLException {
        File file = new File(getHome(), database.getDatabaseName());
        if (!file.exists() || !file.isDirectory()) {
            throw new MySQLException("数据库[" + database + "]有问题");
        }
        return file;
    }

    public static class ManagerInitialization {

        private final Map<Class<? extends MySQLManager<?>>, MySQLManager<?>> allManager = new HashMap<>();

        private final List<MySQLManager<?>> sortedList = new ArrayList<>();

        private final Set<Class<? extends MySQLManager<?>>> added = new HashSet<>();

        private void registerManager(MySQLManager<?> manager) {
            allManager.put((Class<? extends MySQLManager<?>>) manager.getClass(), manager);
        }

        private void managerRefresh() throws MySQLException {
            allManager.forEach(this::doRefresh);
            for (MySQLManager<?> manager : sortedList) {
                manager.refresh();
            }
        }

        private void doRefresh(Class<? extends MySQLManager<?>> clazz, MySQLManager<?> manager) {
            if (added.contains(clazz)) {
                return;
            }
            InitAfter initAfter = clazz.getAnnotation(InitAfter.class);
            if (initAfter != null) {
                Class<? extends MySQLManager<?>> after = initAfter.value();
                MySQLManager<?> first = allManager.get(after);
                if (first == null) {
                    throw new MySQLInitException("没有注册" + after.getName() + "组件");
                }
                doRefresh(after, first);
            }
            this.sortedList.add(manager);
            this.added.add(clazz);

        }


    }


}
