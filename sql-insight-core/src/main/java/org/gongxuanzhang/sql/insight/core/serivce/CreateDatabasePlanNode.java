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

    @Override
    public void doPlan(StorageEngine storageEngine, ExecuteContext context) {
        //Create database folder
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
    * Here is just an example, you need to implement it according to your file system and requirements
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
