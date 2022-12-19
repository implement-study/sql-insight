package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.tool.DbFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * 展示表结构
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DescTable implements Executor {

    private static final String[] TABLE_DESC_HEAD = new String[]{
            "field", "type", "null", "primary key", "default", "auto_increment"};

    private final String tableName;

    public DescTable(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public Result doExecute() {
        File gfrmFile = null;
        try {
            if (tableName.contains(".")) {
                String[] split = tableName.split("\\.");
                gfrmFile = DbFactory.getGfrmFile(split[0], split[1]);
            } else {
                gfrmFile = DbFactory.getGfrmFile(tableName);
            }
            if (!gfrmFile.exists()) {
                return Result.error("表" + tableName + "不存在");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        try (FileInputStream fileInputStream = new FileInputStream(gfrmFile);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            TableInfo tableInfo = (TableInfo) objectInputStream.readObject();
            return Result.select(TABLE_DESC_HEAD, tableInfo.descTable());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
}
