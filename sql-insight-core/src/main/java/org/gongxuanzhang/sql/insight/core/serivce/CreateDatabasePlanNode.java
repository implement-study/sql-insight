package org.gongxuanzhang.sql.insight.core.serivce;

import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;
import org.gongxuanzhang.sql.insight.core.environment.ExecuteContext;
import org.gongxuanzhang.sql.insight.core.optimizer.plan.PlanNode;

import java.io.File;

public class CreateDatabasePlanNode implements PlanNode {
    private final String databaseName;

    public CreateDatabasePlanNode(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public boolean withoutStorageEngine() {

        return true;
    }

    @Override
    public String needStorageEngineName() {

        return null;
    }

    @Override
    public void doPlan(StorageEngine storageEngine, ExecuteContext context) {
        //创建数据库文件夹
        boolean success = createDatabaseFolder(databaseName);
        if (success) {
            //todo Database " + databaseName + " created successfully.
        } else {
            //todo "Failed to create database " + databaseName
        }
    }

    /**
     * 实际的创建数据库文件夹的逻辑
     * 返回 true 表示成功，false 表示失败
     * @param databaseName 数据库名字name
     * @return
     */
    private boolean createDatabaseFolder(String databaseName) {
        File databaseFolder = new File(databaseName);
        if (!databaseFolder.exists() && databaseFolder.mkdirs()) {
            return true;
        }
        return false;
    }
}
