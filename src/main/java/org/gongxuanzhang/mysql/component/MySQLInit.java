package org.gongxuanzhang.mysql.component;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.core.PropertiesConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 如果没配置data dir 给默认值
 * 同时创建文件夹
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class MySQLInit implements EnvironmentPostProcessor {


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String dataDir = environment.getProperty(PropertiesConstant.DATA_DIR);
        if (dataDir == null) {
            File db = new File("db");
            dataDir = db.getAbsolutePath();
            MutablePropertySources propertySources = environment.getPropertySources();
            Map<String, Object> map = new HashMap<>();
            map.put(PropertiesConstant.DATA_DIR, dataDir);
            propertySources.addLast(new MapPropertySource("mysql", map));
        }
        File db = new File(dataDir);
        if (!db.exists()) {
            db.mkdirs();
        }
    }
}
