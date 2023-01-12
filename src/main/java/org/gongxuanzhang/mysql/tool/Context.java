package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.core.EngineSelectable;
import org.gongxuanzhang.mysql.core.MySqlProperties;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.core.manager.DatabaseManager;
import org.gongxuanzhang.mysql.core.manager.EngineManager;
import org.gongxuanzhang.mysql.core.manager.TableManager;
import org.gongxuanzhang.mysql.entity.GlobalProperties;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.EngineException;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.StorageEngine;

import java.io.File;
import java.util.List;

/**
 * 环境辅助类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class Context {


    private static final DatabaseManager DATABASE_MANAGER;
    private static final TableManager TABLE_MANAGER;
    private static final EngineManager ENGINE_MANAGER;

    static {
        try {
            DATABASE_MANAGER = new DatabaseManager();
            ENGINE_MANAGER = new EngineManager();
            TABLE_MANAGER = new TableManager(DATABASE_MANAGER);
            DATABASE_MANAGER.refresh();
            ENGINE_MANAGER.refresh();
            TABLE_MANAGER.refresh();
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
    public static StorageEngine selectStorageEngine(String engineName) throws EngineException {
        if (engineName == null) {
            throw new EngineException("无法获取目标引擎");
        }
        StorageEngine storageEngine = ENGINE_MANAGER.select(engineName);
        if (storageEngine == null) {
            throw new EngineException(engineName + "引擎不存在");
        }
        return storageEngine;
    }


    /**
     * 把一个简单的tokenInfo 填充满
     *
     * @return 一个有完全表信息的table Info
     **/
    public static TableInfo fillTableInfo(TableInfo info) throws MySQLException {
        if (info.getDatabase() == null) {
            info.setDatabase(SessionManager.currentSession().getDatabase());
        }
        String key = info.getDatabase() + "." + info.getTableName();
        TableInfo select = TABLE_MANAGER.select(key);
        if (select == null) {
            throw new MySQLException(key + "表不存在");
        }
        return select;
    }


    /**
     * 通过选择器拿到目标引擎
     **/
    public static StorageEngine selectStorageEngine(EngineSelectable engineSelectable) throws EngineException {
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
    @Deprecated
    public static File getDatabaseHome(String database) throws MySQLException {
        File file = new File(getHome(), database);
        if (!file.exists() || !file.isDirectory()) {
            throw new MySQLException("数据库[" + database + "]有问题");
        }
        return file;
    }


}
