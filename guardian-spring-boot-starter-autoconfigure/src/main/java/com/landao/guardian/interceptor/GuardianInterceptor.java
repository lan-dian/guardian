package com.landao.guardian.interceptor;

import com.landao.guardian.core.LoginHandler;
import com.landao.guardian.core.ThreadStorage;
import com.landao.guardian.core.TokenHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;


public class GuardianInterceptor implements HandlerInterceptor {


    @Resource
    private TokenHandler tokenHandler;

    @Resource
    private LoginHandler loginHandler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取处理method
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        Method method = ((HandlerMethod) handler).getMethod();
        //token初始化
        tokenHandler.initTokenBean();
        //登陆验证
        loginHandler.loginCheck(method);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadStorage.clearAll();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        ThreadStorage.clearAll();
    }

}
