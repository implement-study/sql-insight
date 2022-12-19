package org.gongxuanzhang.mysql.core;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 会话管理器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SessionManager {

    private final static Map<String, MySqlSession> SESSION_BOX = new ConcurrentHashMap<>();


    public static MySqlSession currentSession() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        return SESSION_BOX.computeIfAbsent(requestAttributes.getSessionId(), MySqlSession::new);
    }

}
