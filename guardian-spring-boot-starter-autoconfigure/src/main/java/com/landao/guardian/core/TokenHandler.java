package com.landao.guardian.core;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.landao.guardian.annotations.system.Handler;
import com.landao.guardian.annotations.token.UserId;
import com.landao.guardian.exception.system.GuardianAnnotationException;
import com.landao.guardian.exception.token.TokenBeanException;
import com.landao.guardian.util.JavaTypeUtil;
import com.landao.guardian.util.TokenUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;

@Handler
public class TokenHandler{

    @Resource
    private ApplicationContext applicationContext;


    public void initTokenBean(String token, String privateKey){
        DecodedJWT decodedJwt = TokenUtil.getDecodedJwt(token, privateKey);

        String userType = TokenUtil.getUserType(decodedJwt);

        Object bean = applicationContext.getBean(userType);
        if(!(bean instanceof TokenService)){
            throw new GuardianAnnotationException("用户类型为"+userType+"的认证服务没有继承tokenService");
        }
        TokenService<?,?> tokenService=(TokenService<?,?>)bean;

        Class<?> tokenBeanType = JavaTypeUtil.getFirstGeneraType(token);

        initUserInfo(decodedJwt,tokenBeanType);

        GuardianContext.setUserType(userType);
        GuardianContext.login();
        GuardianContext.setTokenService(tokenService);
    }

    /**
     * 初始化用户所有信息
     */
    private void initUserInfo(DecodedJWT decodedJwt,Class<?> tokenBeanType) {

        Object userBean = null;
        try {
            userBean = tokenBeanType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new TokenBeanException(tokenBeanType.getName() + "没有无参构造器");
        }

        Field[] fields = tokenBeanType.getDeclaredFields();
        for (Field field : fields) {
            setField(field, userBean, decodedJwt);
        }
        GuardianContext.setUser(userBean);

    }

    private void setField(Field field, Object userBean, DecodedJWT decoder) {
        Class<?> fieldType = field.getType();
        Object fieldValue = null;
        if (field.isAnnotationPresent(UserId.class)) {
            String subject = decoder.getSubject();
            Object userId = null;
            if (JavaTypeUtil.isInteger(fieldType)) {
                userId =  Integer.valueOf(subject);
            } else if (JavaTypeUtil.isLong(fieldType)) {
                userId =  Long.valueOf(subject);
            } else if (JavaTypeUtil.isString(fieldType)) {
                userId =  subject;
            }
            GuardianContext.setUserId(userId);
            fieldValue = userId;
        } else {
            fieldValue = decoder.getClaim(field.getName()).as(fieldType);
        }
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, userBean, fieldValue);
    }

}
