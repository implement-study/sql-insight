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

import org.gongxuanzhang.mysql.entity.GlobalProperties;
import org.gongxuanzhang.mysql.exception.SessionException;
import org.gongxuanzhang.mysql.tool.TimedCache;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import static org.gongxuanzhang.mysql.core.MySqlProperties.SESSION_DURATION;


/**
 * 会话管理器
 * 本身不是Spring容器中组件
 * 需要在Spring环境初始化之后初始化 同时要提供静态Api
 * 所以加了一个初始化方法交给Spring生命周期
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SessionManager {

    private static TimedCache<String, MySqlSession> SESSION_BOX;


    public static void init() {
        GlobalProperties global = GlobalProperties.getInstance();
        String sessionCount = global.get(MySqlProperties.MAX_SESSION_COUNT);
        SESSION_BOX = new TimedCache<>(Integer.parseInt(sessionCount));
    }


    public static MySqlSession currentSession() throws SessionException {
        RequestAttributes requestAttributes = null;
        try {
            requestAttributes = RequestContextHolder.currentRequestAttributes();
        } catch (Exception e) {
            return new MySqlSession("virtual");
        }

        MySqlSession mySqlSession = SESSION_BOX.get(requestAttributes.getSessionId());
        if (mySqlSession != null) {
            return mySqlSession;
        }
        String sessionId = requestAttributes.getSessionId();
        GlobalProperties global = GlobalProperties.getInstance();
        mySqlSession = new MySqlSession(sessionId);
        if (!SESSION_BOX.put(sessionId, mySqlSession, Integer.parseInt(global.get(SESSION_DURATION)))) {
            throw new SessionException("会话创建失败");
        }
        return mySqlSession;
    }


}
