package org.gongxuanzhang.mysql.core;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * 默认变量key
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public enum MySqlProperties {

    /**
     * 文件根目录
     **/
    DATA_DIR("dataDir", new File("db").getAbsolutePath(), true),

    /**
     * 默认存储引擎
     **/
    DEFAULT_STORAGE_ENGINE("default-storage-engine", "fool", true),

    /**
     * 最大会话数
     **/
    MAX_SESSION_COUNT("max-session-count", "10", true),

    /**
     * session持续时间,不代表到时就过期，当会话数量超过 MAX_SESSION_COUNT
     * 同时有过期会话才会过期
     **/
    SESSION_DURATION("session-duration", Long.toString(TimeUnit.MINUTES.toMillis(10)), true);

    public final String key;

    public final boolean readonly;

    public final String defaultValue;

    MySqlProperties(String key, String defaultValue, boolean readonly) {
        this.key = key;
        this.readonly = readonly;
        this.defaultValue = defaultValue;
    }
}
