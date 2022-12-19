package org.gongxuanzhang.mysql.core;

/**
 * 常量
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface PropertiesConstant {

    /**
     * 文件根目录
     **/
    String DATA_DIR = "dataDir";

    /**
     * 默认存储引擎
     **/
    String DEFAULT_STORAGE_ENGINE = "default-storage-engine";

    /**
     * 最大会话数
     **/
    String MAX_SESSION_COUNT = "max-session-count";

    /**
     * session持续时间,不代表到时就过期，当会话数量超过 MAX_SESSION_COUNT
     * 同时有过期会话才会过期
     *
     **/
    String SESSION_DURATION = "session-duration";

}
