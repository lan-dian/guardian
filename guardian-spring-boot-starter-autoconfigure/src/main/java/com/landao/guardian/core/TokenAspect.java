package com.landao.guardian.core;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.landao.guardian.core.TokenService;
import com.landao.guardian.annotations.GuardianService;
import com.landao.guardian.annotations.UserId;
import com.landao.guardian.consts.TokenConst;
import com.landao.guardian.util.JavaTypeUtil;
import com.landao.guardian.util.NewxWebUtil;
import com.landao.guardian.util.TokenUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;


@Aspect
@Order(0)
public class TokenAspect {

    @Resource
    ApplicationContext applicationContext;

    public static final String or="||";

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)"
            +or+"@annotation(org.springframework.stereotype.Controller)")
    public void pointCut() {

    }

    @Before(value = "pointCut()")
    public void logBeforeController(JoinPoint joinPoint) {
        Map<String, Object> tokenServices = applicationContext.getBeansWithAnnotation(GuardianService.class);
        if(TokenUtil.hasToken()){
            String token = TokenUtil.getToken();
            String userType = TokenUtil.getUserType(token);
            Collection<Object> services = tokenServices.values();
            //todo 升级为在注册组件的时候，把beanName设置为userType从而直接获取
            for (Object service : services) {
                GuardianService guardianService = AnnotationUtils.findAnnotation(service.getClass(), GuardianService.class);
                if(Objects.equals(userType, Objects.requireNonNull(guardianService).userType())){
                    TokenService<?,?> tokenService = (TokenService<?,?>) service;
                    tokenService.initUserInfo(token,userType);
                }
            }
        }
    }





}
