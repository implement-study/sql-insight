package org.gongxuanzhang.mysql.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.Assert;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.gongxuanzhang.mysql.core.PropertiesConstant.DATA_DIR;
import static org.gongxuanzhang.mysql.core.PropertiesConstant.DEFAULT_STORAGE_ENGINE;

/**
 * 如果没配置 就给默认值
 * 同时创建文件夹
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class MySQLInit implements EnvironmentPostProcessor {

    private static Map<String, String> DEFAULT_PROPERTIES = new HashMap<>();

    static {
        DEFAULT_PROPERTIES.put(DATA_DIR, new File("db").getAbsolutePath());
        DEFAULT_PROPERTIES.put(DEFAULT_STORAGE_ENGINE, "json");
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> extra = new HashMap<>(DEFAULT_PROPERTIES.size());
        DEFAULT_PROPERTIES.forEach((key, defaultValue) -> {
            if (!environment.containsProperty(key)) {
                extra.put(key, defaultValue);
            }
        });

        if (!extra.isEmpty()) {
            MutablePropertySources propertySources = environment.getPropertySources();
            propertySources.addLast(new MapPropertySource("mysql", extra));
        }
        String dataDir = environment.getProperty(DATA_DIR);
        Assert.notNull(dataDir, "无法解析出data Dir");
        File db = new File(dataDir);
        if (!db.exists()) {
            db.mkdirs();
        }
    }
}
