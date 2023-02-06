package org.gongxuanzhang.mysql.core.result;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 单列返回
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SingleRowResult extends SelectResult {

    public SingleRowResult(String head, List<String> data) {
        super(new String[]{head},
                data.stream().map(d -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(head, d);
                    return jsonObject;
                }).collect(Collectors.toList()));

    }


}
