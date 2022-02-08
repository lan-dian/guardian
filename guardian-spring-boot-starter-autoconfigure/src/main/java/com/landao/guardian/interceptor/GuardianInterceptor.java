package com.landao.guardian.interceptor;

import com.landao.guardian.config.GuardianProperties;
import com.landao.guardian.core.handler.BanHandler;
import com.landao.guardian.core.handler.LoginHandler;
import com.landao.guardian.core.handler.AuthorHandler;
import com.landao.guardian.core.GuardianContext;
import com.landao.guardian.core.handler.TokenHandler;
import com.landao.guardian.core.interfaces.GuardianHandler;
import com.landao.guardian.util.TokenUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;


public class GuardianInterceptor implements HandlerInterceptor {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private GuardianProperties guardianProperties;

    @Resource
    private TokenHandler tokenHandler;

    @Resource
    private LoginHandler loginHandler;

    @Resource
    private AuthorHandler authorHandler;

    @Resource
    private BanHandler banHandler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取处理method
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        GuardianContext.clear();//必然成功的保障

        Method method = ((HandlerMethod) handler).getMethod();

        GuardianProperties.Token tokenProperties = guardianProperties.getToken();

        String header = request.getHeader(tokenProperties.getHeaderName());
        if(StringUtils.hasText(header)){
            String token = TokenUtils.getValidToken(header, tokenProperties.getPrefix());
            //token初始化
            tokenHandler.initTokenBean(token,tokenProperties.getPrivateKey());
        }

        if(GuardianContext.isLogin()){
            banHandler.checkBan();
        }

        //登陆验证
        loginHandler.loginCheck(method);

        //权限验证
        authorHandler.checkAuthor(method);

        //设置额外字段
        if(GuardianContext.isLogin()){
            GuardianContext.getTokenService().setExtra();
        }

        Map<String, GuardianHandler> handlers = applicationContext.getBeansOfType(GuardianHandler.class);

        for (GuardianHandler guardianHandler : handlers.values().stream()
                .sorted(Comparator.comparingInt(Ordered::getOrder))
                .collect(Collectors.toList())) {
            boolean keepOn = guardianHandler.handler(method);
            if(!keepOn){
                break;
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        GuardianContext.clear();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        GuardianContext.clear();
    }

}
