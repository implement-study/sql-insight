package org.gongxuanzhang.mysql.tool;


import org.gongxuanzhang.mysql.core.MySqlSession;
import org.gongxuanzhang.mysql.core.PropertiesConstant;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.entity.GlobalProperties;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.ExecuteException;

import java.io.File;

/**
 * 数据库相关信息工厂
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DbFactory {

    private final static String GFRM_SUFFIX = ".gfrm";

    private DbFactory() {

    }


    /**
     * 通过表信息拿到表信息文件
     *
     * @param tableInfo 表信息
     * @return 表信息文件
     * @throws ExecuteException 过程中出现问题会报错
     **/
    public static File getGfrmFile(TableInfo tableInfo) throws ExecuteException {
        return getGfrmFile(tableInfo.getDatabase(), tableInfo.getTableName());
    }

    /**
     * 当没指定数据库时从上下文获取当前使用的数据库
     **/
    public static File getGfrmFile(String tableName) throws ExecuteException {
        MySqlSession currentSession = SessionManager.currentSession();
        return getGfrmFile(currentSession.getDatabase(), tableName);
    }

    public static File getGfrmFile(String database, String tableName) throws ExecuteException {
        File db = ContextSupport.getHome();
        if (database == null) {
            database = SessionManager.currentSession().getDatabase();
        }
        checkDatabase(database);
        File dataBaseDir = new File(db, database);
        return new File(dataBaseDir, tableName + GFRM_SUFFIX);
    }


    private static void checkDatabase(String database) throws ExecuteException {
        if (database == null) {
            throw new ExecuteException("获取不到database，请先使用use database");
        }
        GlobalProperties properties = GlobalProperties.getInstance();
        String dataDir = properties.get(PropertiesConstant.DATA_DIR);
        File dataBaseDir = new File(dataDir, database);
        if (!dataBaseDir.exists()) {
            throw new ExecuteException("数据库[" + database + "]不存在");
        }
    }
}
