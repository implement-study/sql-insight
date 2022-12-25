package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.core.MySqlProperties;
import org.gongxuanzhang.mysql.entity.GlobalProperties;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 环境辅助类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class ContextSupport {

    private ContextSupport() {

    }

    private static Set<String> databases;

    public static Set<String> getDatabases() {
        if (databases != null) {
            return databases;
        }
        File home = getHome();
        File[] dbs = home.listFiles(File::isDirectory);
        databases = new HashSet<>();
        if (dbs == null) {
            return databases;
        }
        for (File db : dbs) {
            databases.add(db.getName());
        }
        return databases;
    }

    /**
     * 当数据库数据更新的时候 重新加载缓存
     **/
    public static void refreshDatabases() {
        databases = null;
    }

    /**
     * 获得数据库根目录
     *
     * @return 返回个啥
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


}
