package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.core.PropertiesConstant;
import org.gongxuanzhang.mysql.entity.GlobalProperties;

import java.io.File;

/**
 * 环境辅助类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class ContextSupport {

    private ContextSupport() {

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
