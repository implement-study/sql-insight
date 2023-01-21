package org.gongxuanzhang.mysql.core;

import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.mysql.tool.Pair;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 排序字段
 * 用于辅助查询
 *
 * @author gongxuanzhang
 */
public class Order implements Comparator<JSONObject>, Available {


    private final List<Pair<String, Class<?>>> orderBy = new ArrayList<>();


    @Override
    public int compare(JSONObject o1, JSONObject o2) {
        for (Pair<String, Class<?>> order : orderBy) {

        }

        return;
    }

    @Override
    public boolean available() {
        return !CollectionUtils.isEmpty(orderBy);
    }

    public void addColumn(String colName, Class<?> type) {
        this.orderBy.add(Pair.of(colName, type));
    }

    private int <T> objectCompare(Object o1, Object o2) {
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        if(o1 instanceof Comparable){
            return ((Comparable) o1).compareTo(o2);
        }
        return 0;
    }
}
