package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.core.PropertiesConstant;
import org.gongxuanzhang.mysql.entity.GlobalProperties;

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
     * 获得数据库根目录
     *
     * @return 返回个啥
     **/
    public static File getHome() {
        GlobalProperties properties = GlobalProperties.getInstance();
        String dataDir = properties.get(PropertiesConstant.DATA_DIR);
        return new File(dataDir);
    }


}
