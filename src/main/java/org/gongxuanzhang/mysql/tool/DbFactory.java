package org.gongxuanzhang.mysql.tool;


import org.gongxuanzhang.mysql.core.MySqlProperties;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.entity.GlobalProperties;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.ExecuteException;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.io.File;

/**
 * 数据库相关信息工厂
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DbFactory {

    /**
     * 在mysql的frm文件上加了个我的姓 嘿嘿
     **/
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
        File db = ContextSupport.getHome();
        if (tableInfo.getDatabase() == null) {
            try {
                String database = SessionManager.currentSession().getDatabase();
                tableInfo.setDatabase(database);
            } catch (MySQLException e) {
                e.printStackTrace();
                throw new ExecuteException(e.getMessage());
            }
        }
        checkDatabase(tableInfo.getDatabase());
        File dataBaseDir = new File(db, tableInfo.getDatabase());
        return new File(dataBaseDir, tableInfo.getTableName() + GFRM_SUFFIX);
    }




    private static void checkDatabase(String database) throws ExecuteException {
        if (database == null) {
            throw new ExecuteException("获取不到database，请先使用use database");
        }
        GlobalProperties properties = GlobalProperties.getInstance();
        String dataDir = properties.get(MySqlProperties.DATA_DIR);
        File dataBaseDir = new File(dataDir, database);
        if (!dataBaseDir.exists()) {
            throw new ExecuteException("数据库[" + database + "]不存在");
        }
    }
}
