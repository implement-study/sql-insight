package org.gongxuanzhang.mysql.entity;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 插入数据
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class InsertData {

    private final List<JSONObject> data;

    private Map<String, Set<Object>> unique = new HashMap<>();

    private final Set<String> uniqueKeys;

    private List<String> insertStr;


    public InsertData(List<JSONObject> data, Set<String> uniqueKeys) {
        this.data = data;
        this.uniqueKeys = uniqueKeys;
        if (!uniqueKeys.isEmpty()) {
            data.forEach(this::fillUnique);
        }

    }


    /**
     * 当前插入数据的唯一列 是否包含目标值
     * 例： 解析出需要插入的数据是 {id:1}
     * id是唯一列，但是数据中已经有了id是1的数据  就会插入失败
     *
     * @return true 包含
     **/
    public boolean containsUnique(String uniqueKey, Object value) {
        if (unique.isEmpty()) {
            return false;
        }
        Set<Object> uniqueSet = unique.get(uniqueKey);
        if (CollectionUtils.isEmpty(uniqueSet)) {
            return false;
        }
        return uniqueSet.contains(value);
    }

    public List<String> getInsertStr() {
        if (insertStr == null) {
            insertStr = getData().stream().map(JSONObject::toString).collect(Collectors.toList());
        }
        return insertStr;
    }


    private void fillUnique(JSONObject data) {
        for (String uniqueKey : uniqueKeys) {
            Object uniqueValue = data.get(uniqueKey);
            if (uniqueValue != null) {
                this.unique.computeIfAbsent(uniqueKey, (k) -> new HashSet<>()).add(uniqueValue);
            }
        }
    }

}
