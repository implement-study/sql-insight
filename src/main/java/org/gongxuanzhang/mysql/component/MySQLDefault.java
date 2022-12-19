package org.gongxuanzhang.mysql.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.Assert;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.gongxuanzhang.mysql.core.PropertiesConstant.DATA_DIR;
import static org.gongxuanzhang.mysql.core.PropertiesConstant.DEFAULT_STORAGE_ENGINE;
import static org.gongxuanzhang.mysql.core.PropertiesConstant.MAX_SESSION_COUNT;
import static org.gongxuanzhang.mysql.core.PropertiesConstant.SESSION_DURATION;

/**
 * 如果没配置 就给默认值
 * 同时创建文件夹
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class MySQLDefault implements EnvironmentPostProcessor, ApplicationListener<ApplicationStartedEvent> {

    private static final Map<String, String> DEFAULT_PROPERTIES = new HashMap<>();

    static {
        DEFAULT_PROPERTIES.put(DATA_DIR, new File("db").getAbsolutePath());
        DEFAULT_PROPERTIES.put(DEFAULT_STORAGE_ENGINE, "json");
        DEFAULT_PROPERTIES.put(MAX_SESSION_COUNT, "10");
        DEFAULT_PROPERTIES.put(SESSION_DURATION, Long.toString(TimeUnit.MINUTES.toMillis(10)));
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

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println(1);
    }
}
