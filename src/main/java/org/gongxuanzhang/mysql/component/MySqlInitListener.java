package org.gongxuanzhang.mysql.component;

import org.gongxuanzhang.mysql.annotation.Engine;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.storage.StorageEngine;
import org.gongxuanzhang.mysql.tool.Context;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class MySqlInitListener implements ApplicationListener<ApplicationStartedEvent> {

    private final Map<String, StorageEngine> engineMap;

    public MySqlInitListener(@Engine Map<String, StorageEngine> engineMap) {
        this.engineMap = engineMap;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        engineMap.forEach((k, v) -> Context.registerEngine(v));
        SessionManager.init();
    }
}
