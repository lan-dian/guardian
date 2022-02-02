package com.landao.guardian.core;


import com.landao.guardian.annotations.system.GuardianService;
import com.landao.guardian.annotations.system.Handler;
import com.landao.guardian.util.GuardianContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

@Handler
public class TokenHandler {

    @Resource
    private ApplicationContext applicationContext;


    @SuppressWarnings("rawtypes")
    public void initTokenBean(){
        if(GuardianContext.hasToken()){
            String token = GuardianContext.getToken();
            String userType = GuardianContext.getUserType(token);

            Map<String, Object> tokenServices = applicationContext.getBeansWithAnnotation(GuardianService.class);
            //todo 升级为在注册组件的时候，把beanName设置为userType从而直接获取
            for (Object service : tokenServices.values()) {
                GuardianService guardianService = AnnotationUtils.findAnnotation(service.getClass(), GuardianService.class);
                if(Objects.equals(userType, Objects.requireNonNull(guardianService).userType())){
                    TokenService tokenService = (TokenService) service;
                    tokenService.initUserInfo(token,userType);
                }
            }
        }
    }



}
