package org.gongxuanzhang.mysql.core;

import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 会话信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class MySqlSession {

    private final String id;

    private final Map<String, String> attr = new HashMap<>();

    public MySqlSession(String id) {
        this.id = id;
    }

    public Map<String, String> getAllAttr(){
        return Collections.unmodifiableMap(attr);
    }

    public void set(String key, String value) {
        attr.put(key, value);
    }

    public String get(String key) {
        return attr.get(key);
    }

    public void useDatabase(String database) {
        attr.put("database", database);
    }


    public String getDatabase() {
        return attr.get("database");
    }


    @Override
    public String toString() {
        return "MySqlSession{" +
                "id='" + id + '\'' +
                '}';
    }
}
