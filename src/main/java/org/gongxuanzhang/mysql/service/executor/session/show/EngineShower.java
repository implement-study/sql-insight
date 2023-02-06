package org.gongxuanzhang.mysql.service.executor.session.show;

import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.storage.StorageEngine;
import org.gongxuanzhang.mysql.tool.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        List<JSONObject> data =
                Context.getEngineList()
                        .stream()
                        .map(this::engineToResult)
                        .collect(Collectors.toList());
        return Result.select(HEAD, data);
    }

    private JSONObject engineToResult(StorageEngine engine) {
        JSONObject jsonObject = new JSONObject(8);
        jsonObject.put(HEAD[0], engine.getEngineName());
        jsonObject.put(HEAD[1], Boolean.toString(engine.supportTransaction()));
        return jsonObject;
    }
}
