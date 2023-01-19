package org.gongxuanzhang.mysql.tool;


import org.gongxuanzhang.mysql.core.MySqlProperties;
import org.gongxuanzhang.mysql.entity.GlobalProperties;
import org.gongxuanzhang.mysql.exception.ExecuteException;

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
