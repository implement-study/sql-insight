package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.service.MetaData;
import org.gongxuanzhang.mysql.service.Result;
import org.gongxuanzhang.mysql.service.SelectResult;
import org.gongxuanzhang.mysql.storage.StorageEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SelectExecutor implements Executor {

    /**
     * 要查询的表名
     **/
    private String tableName;

    /**
     * 查询目标字段
     **/
    private List<String> targetColumn;

    /**
     * 别名
     **/
    private Map<String, String> alias;


    @Override
    public Result doExecute(StorageEngine storageEngine) {
        SelectResult result = new SelectResult(200, null);
        List<MetaData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, String> stringStringMap = new HashMap<>();
            stringStringMap.put("id", i + "");
            MetaData metaData = new MetaData();
            metaData.setMap(stringStringMap);
            list.add(metaData);
        }
        result.setData(list);
        return result;
    }
}
