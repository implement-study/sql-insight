package org.gongxuanzhang.mysql.storage.innodb;

import org.gongxuanzhang.mysql.annotation.Engine;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.DeleteInfo;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.entity.SingleSelectInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.UpdateInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.CreateTableEngine;
import org.gongxuanzhang.mysql.storage.DeleteEngine;
import org.gongxuanzhang.mysql.storage.InsertEngine;
import org.gongxuanzhang.mysql.storage.SelectEngine;
import org.gongxuanzhang.mysql.storage.StorageEngine;
import org.gongxuanzhang.mysql.storage.UpdateEngine;

/**
 * InnoDB引擎实现
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Engine
public class InnoDb implements StorageEngine {

    private final CreateTableEngine creator = new InnoDbTableCreator();

    private final InsertEngine inserter = new InnoDbInsert();

    private final SelectEngine selector = new InnoDbSelect();

    private final UpdateEngine updater = new InnoDbUpdate();

    private final DeleteEngine deleter = new InnoDbDelete();

    @Override
    public Result createTable(TableInfo info) throws MySQLException {
        return creator.createTable(info);
    }

    @Override
    public Result delete(DeleteInfo info) throws MySQLException {
        return deleter.delete(info);
    }

    @Override
    public Result insert(InsertInfo info) throws MySQLException {
        return inserter.insert(info);
    }

    @Override
    public Result select(SingleSelectInfo info) throws MySQLException {
        return selector.select(info);
    }

    @Override
    public Result update(UpdateInfo info) throws MySQLException {
        return updater.update(info);
    }

    @Override
    public String getEngineName() {
        return "innodb";
    }

    @Override
    public boolean supportTransaction() {
        return true;
    }


}
