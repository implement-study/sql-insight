package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.entity.PrimaryKey;
import org.gongxuanzhang.mysql.entity.TableInfo;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface HavePrimaryKey {

    /**
     * 拿到肚子里的主键
     *
     * @param tableInfo 表信息
     * @return 主键
     **/
    PrimaryKey getPrimaryKey(TableInfo tableInfo);
}
