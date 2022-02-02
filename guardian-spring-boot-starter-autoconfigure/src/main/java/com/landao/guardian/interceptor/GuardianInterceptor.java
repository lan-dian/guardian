package com.landao.guardian.interceptor;

import com.landao.guardian.config.GuardianProperties;
import com.landao.guardian.core.LoginHandler;
import com.landao.guardian.core.RoleHandler;
import com.landao.guardian.core.context.CurrentSubject;
import com.landao.guardian.core.TokenHandler;
import com.landao.guardian.util.TokenUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;


public class GuardianInterceptor implements HandlerInterceptor {

    @Resource
    private GuardianProperties guardianProperties;

    @Resource
    private TokenHandler tokenHandler;

    @Resource
    private LoginHandler loginHandler;

    @Resource
    private RoleHandler roleHandler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取处理method
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        Method method = ((HandlerMethod) handler).getMethod();

        GuardianProperties.Token tokenProperties = guardianProperties.getToken();

        String header = request.getHeader(tokenProperties.getHeaderName());
        if(StringUtils.hasText(header)){
            String token = TokenUtil.getValidToken(header, tokenProperties.getPrefix());
            //token初始化
            tokenHandler.initTokenBean(token,tokenProperties.getPrivateKey());
        }

        //登陆验证
        loginHandler.loginCheck(method);

        //角色验证
        roleHandler.checkRole(method);

        //设置额外字段
        CurrentSubject.getTokenService().setExtra();

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        CurrentSubject.clearAll();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        CurrentSubject.clearAll();
    }

}
