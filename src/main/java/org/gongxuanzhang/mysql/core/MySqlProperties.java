/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    SESSION_DURATION("session-duration", Long.toString(TimeUnit.MINUTES.toMillis(10)), true),

    /**
     * 默认存储引擎
     **/
    STORAGE_ENGINE("storage_engine","innodb",false);

    public final String key;

    public final boolean readonly;

    public final String defaultValue;

    MySqlProperties(String key, String defaultValue, boolean readonly) {
        this.key = key;
        this.readonly = readonly;
        this.defaultValue = defaultValue;
    }
}
