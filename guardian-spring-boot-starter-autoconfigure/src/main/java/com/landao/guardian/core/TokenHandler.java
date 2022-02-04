package com.landao.guardian.core;


import com.landao.guardian.annotations.system.GuardianService;
import com.landao.guardian.annotations.system.Handler;
import com.landao.guardian.consts.GuardianConst;
import com.landao.guardian.core.context.CurrentSubject;
import com.landao.guardian.exception.system.GuardianAnnotationException;
import com.landao.guardian.util.TokenUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

@Handler
public class TokenHandler{

    @Resource
    private ApplicationContext applicationContext;


    @SuppressWarnings("rawtypes")
    public void initTokenBean(String token,String privateKey){
        String userType = TokenUtil.getUserType(token,privateKey);

        TokenService tokenService =(TokenService) applicationContext.getBean(GuardianConst.beanPrefix + userType);
        /*if(tokenService==null){
            throw new GuardianAnnotationException("没有为"+userType+"类型的用户注册tokenService到ioc容器中");
        }*/
        // GuardianService guardianService = AnnotationUtils.findAnnotation(service.getClass(), GuardianService.class);
        tokenService.initUserInfo(token,privateKey);
        CurrentSubject.setUserType(userType);
    }

}
