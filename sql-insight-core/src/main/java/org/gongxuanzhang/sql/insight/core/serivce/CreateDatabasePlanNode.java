package org.gongxuanzhang.sql.insight.core.serivce;

import org.gongxuanzhang.sql.insight.core.engine.storage.StorageEngine;
import org.gongxuanzhang.sql.insight.core.environment.ExecuteContext;
import org.gongxuanzhang.sql.insight.core.optimizer.plan.PlanNode;

import java.io.File;

/**
 * @author Bryan yang y51288033@gmail.com
 * Create database plan
 */
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

    /**
     * do plan
     * @param storageEngine if {@link this#withoutStorageEngine()} is true, the param is null
     *                      else is engine what is plan node needed
     * @param context       execute context ,sharded in chain
     */
    @Override
    public void doPlan(StorageEngine storageEngine, ExecuteContext context) {
        // 创建数据库文件夹的逻辑
        boolean success = createDatabaseFolder(databaseName);
        if (success) {
            //todo Database " + databaseName + " created successfully.
        } else {
            //todo "Failed to create database " + databaseName
        }
    }

    /**
    * The actual logic of creating the database folder
    * Return true for success, false for failure
    * @param databaseName database name name
    * @return boolean
    */
    private boolean createDatabaseFolder(String databaseName) {
        File databaseFolder = new File(databaseName);
        if (!databaseFolder.exists() && databaseFolder.mkdirs()) {
            return true;
        }
        return false;
    }
}
