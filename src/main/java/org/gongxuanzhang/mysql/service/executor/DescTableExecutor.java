package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.ExecuteException;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.tool.DbFactory;
import org.gongxuanzhang.mysql.tool.ExceptionThrower;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * 展示表结构
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DescTableExecutor implements Executor {

    private static final String[] TABLE_DESC_HEAD = new String[]{
            "field", "type", "null", "primary key", "default", "auto_increment"};

    private final TableInfo info;

    public DescTableExecutor(TableInfo info) {
        this.info = info;
    }

    @Override
    public Result doExecute() throws MySQLException {
        String tableName = info.getTableName();
        String database = info.getDatabase();
        TableInfo info = new TableInfo();
        info.setTableName(tableName);
        info.setDatabase(database);
        File gfrmFile = DbFactory.getGfrmFile(info);
        if (!gfrmFile.exists()) {
            throw new ExecuteException(String.format("表%s不存在", info.getTableName()));
        }
        try (FileInputStream fileInputStream = new FileInputStream(gfrmFile);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            TableInfo tableInfo = (TableInfo) objectInputStream.readObject();
            return Result.select(TABLE_DESC_HEAD, tableInfo.descTable());
        } catch (Exception e) {
            return ExceptionThrower.errorSwap(e);
        }
    }
}
