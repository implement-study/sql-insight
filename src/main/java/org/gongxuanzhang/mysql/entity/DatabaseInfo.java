package org.gongxuanzhang.mysql.entity;

import lombok.Data;
import org.gongxuanzhang.mysql.annotation.DependOnContext;
import org.gongxuanzhang.mysql.tool.Context;

import java.io.File;


/**
 * 数据库信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class DatabaseInfo implements ExecuteInfo {


    private final String databaseName;

    public DatabaseInfo(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * 返回数据库目标位置文件夹
     **/
    @DependOnContext
    public File sourceFile() {
        File home = Context.getHome();
        return new File(home, databaseName);
    }


}
