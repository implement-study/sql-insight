package org.gongxuanzhang.sql.insight.core.serivce;


import org.gongxuanzhang.sql.insight.core.optimizer.plan.ExecutionPlan;
import org.gongxuanzhang.sql.insight.core.optimizer.plan.PlanChain;
import org.gongxuanzhang.sql.insight.core.optimizer.plan.PlanNode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Bryan yang y51288033@gmail.com
 * Create Create database execution plan
 */
public class CreateDatabaseExecutionPlan implements ExecutionPlan {
    private final String databaseName;

    public CreateDatabaseExecutionPlan(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public String showExplain() {
        return "Create database: " + databaseName;
    }

    private class PlanChainImpl implements PlanChain {
        private List<PlanNode> nodes = new ArrayList<>();

        @NotNull
        @Override
        public Iterator<PlanNode> iterator() {
            return nodes.iterator();
        }

        @Override
        public void addNode(PlanNode node) {
            nodes.add(node);
        }
    }

    public PlanChain getPlanChain() {
        PlanChain planChain = new PlanChainImpl();

        // 创建数据库的节点
        PlanNode createDatabaseNode = new CreateDatabasePlanNode(databaseName);
        planChain.addNode(createDatabaseNode);

        return planChain;
    }
}

