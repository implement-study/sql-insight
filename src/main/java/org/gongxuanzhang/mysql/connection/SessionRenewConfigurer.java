package org.gongxuanzhang.mysql.connection;

import org.gongxuanzhang.mysql.core.SessionManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gongxuanzhang
 */
@Configuration
public class SessionRenewConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionRenew()).addPathPatterns("/**");
    }


    public static class SessionRenew implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            SessionManager.currentSession();
            return true;
        }
    }
}
