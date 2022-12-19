package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.entity.GlobalProperties;
import org.gongxuanzhang.mysql.exception.SessionException;
import org.gongxuanzhang.mysql.tool.TimedCache;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import static org.gongxuanzhang.mysql.core.PropertiesConstant.SESSION_DURATION;


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
        String sessionCount = global.get(PropertiesConstant.MAX_SESSION_COUNT);
        SESSION_BOX = new TimedCache<>(Integer.parseInt(sessionCount));
    }


    public static MySqlSession currentSession() throws SessionException {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
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
