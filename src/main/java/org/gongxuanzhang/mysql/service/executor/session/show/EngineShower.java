package org.gongxuanzhang.mysql.service.executor.session.show;

import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.storage.StorageEngine;
import org.gongxuanzhang.mysql.tool.ContextSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * show engines
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class EngineShower implements Shower {


    private static final String[] HEAD = new String[]{"name", "transaction"};


    @Override
    public Result show() {
        List<Map<String, String>> data =
                ContextSupport.getEngineList()
                        .stream()
                        .map(this::engineToResult)
                        .collect(Collectors.toList());
        return Result.select(HEAD, data);
    }

    private Map<String, String> engineToResult(StorageEngine engine) {
        Map<String, String> map = new HashMap<>(8);
        map.put(HEAD[0], engine.getEngineName());
        map.put(HEAD[1], Boolean.toString(engine.supportTransaction()));
        return map;
    }
}
