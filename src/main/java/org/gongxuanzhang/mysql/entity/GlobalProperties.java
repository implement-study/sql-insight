package org.gongxuanzhang.mysql.entity;

import org.gongxuanzhang.mysql.core.MySqlProperties;
import org.gongxuanzhang.mysql.exception.VariableException;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 全局配置
 * 融入spring env
 * 如果在启动之后直接修改了变量
 * 修改properties 变量即可
 * 查询的时候 优先查询properties 其次查询Spring 容器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class GlobalProperties implements EnvironmentAware {

    public static GlobalProperties instance;

    private final Map<String, String> properties = new HashMap<>(64);

    private Environment environment;

    private static final Set<String> READONLY = new HashSet<>();


    @PostConstruct
    public void init() {
        for (MySqlProperties key : MySqlProperties.values()) {
            if (key.readonly) {
                READONLY.add(key.key);
            }
            properties.put(key.key, environment.getProperty(key.key));
        }
    }


    public GlobalProperties() {
        instance = this;
    }

    public static GlobalProperties getInstance() {
        return instance;
    }

    public void set(String key, String value) throws VariableException {
        if (READONLY.contains(key)) {
            throw new VariableException(key + "是只读的，不能修改");
        }
        properties.put(key, value);
    }

    public String get(String key) {
        String value = properties.get(key);
        if (value != null) {
            return value;
        }
        return environment.getProperty(key);
    }

    public String get(MySqlProperties key) {
        return get(key.key);
    }

    public Map<String, String> getAllAttr() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
