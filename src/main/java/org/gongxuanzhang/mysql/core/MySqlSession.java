package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.exception.MySQLException;

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

    private String database;

    private String sql;

    public MySqlSession(String id) {
        this.id = id;
    }

    public Map<String, String> getAllAttr() {
        return Collections.unmodifiableMap(attr);
    }

    public void set(String key, String value) {
        attr.put(key, value);
    }

    public String get(String key) {
        return attr.get(key);
    }

    public void useDatabase(String database) {
        this.database = database;
    }

    public void setSql(String sql){
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public String getDatabase() throws MySQLException {
        if (this.database == null) {
            throw new MySQLException("无法获取 database");
        }
        return this.database;
    }


    @Override
    public String toString() {
        return "MySqlSession{" +
                "id='" + id + '\'' +
                '}';
    }
}
