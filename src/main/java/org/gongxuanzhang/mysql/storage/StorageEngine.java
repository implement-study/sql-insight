package org.gongxuanzhang.mysql.storage;

import org.gongxuanzhang.mysql.annotation.DependOnContext;
import org.gongxuanzhang.mysql.storage.fool.Fool;
import org.gongxuanzhang.mysql.storage.innodb.InnoDb;

/**
 * 存储引擎接口
 * 默认实现有Innodb，Fool
 * 可以自定义
 *
 * @author gxz gongxuanzhang@foxmail.com
 * @see Fool
 * @see InnoDb
 **/
@DependOnContext
public interface StorageEngine extends CreateTableEngine, InsertEngine, DeleteEngine, SelectEngine, UpdateEngine {


    /**
     * 引擎名称
     *
     * @return name
     **/
    String getEngineName();

    /**
     * 引擎是否支持事务
     *
     * @return true 是支持
     **/
    boolean supportTransaction();


}
