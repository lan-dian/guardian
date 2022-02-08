package com.landao.guardian.core.handler;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.landao.guardian.annotations.system.Handler;
import com.landao.guardian.annotations.token.UserId;
import com.landao.guardian.consts.GuardianConst;
import com.landao.guardian.consts.TokenConst;
import com.landao.guardian.core.GuardianContext;
import com.landao.guardian.core.TokenService;
import com.landao.guardian.exception.author.UnLoginException;
import com.landao.guardian.exception.system.GuardianAnnotationException;
import com.landao.guardian.exception.token.TokenBeanException;
import com.landao.guardian.util.RedisUtils;
import com.landao.guardian.util.TypeUtils;
import com.landao.guardian.util.TokenUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;


@Handler
public class TokenHandler{

    @Resource
    private ApplicationContext applicationContext;


    public void initTokenBean(String token, String privateKey){
        DecodedJWT decodedJwt = TokenUtils.getDecodedJwt(token, privateKey);

        GuardianContext.setDecodedJWT(decodedJwt);

        String userType = decodedJwt.getSubject();

        Object bean = applicationContext.getBean(userType);
        if(!(bean instanceof TokenService)){
            throw new GuardianAnnotationException("用户类型为"+userType+"的认证服务没有继承tokenService");
        }
        TokenService<?,?> tokenService=(TokenService<?,?>)bean;

        Class<?> tokenBeanType = TypeUtils.getFirstGeneraType(token);

        initUserInfo(decodedJwt,tokenBeanType);

        Object time = RedisUtils.get(GuardianConst.redisPrefix + ":"
                + userType + ":"
                + GuardianContext.getUserId(),Long.class);
        if(time!=null){
            long publishTime = GuardianContext.getPublishTime();
            if(publishTime<(long) time){
                throw new UnLoginException("该token已失效");
            }
        }

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
        Object fieldValue;
        if (field.isAnnotationPresent(UserId.class)) {
            fieldValue=decoder.getClaim(TokenConst.userId).as(fieldType);
            GuardianContext.setUserId(fieldValue);
        } else {
            fieldValue = decoder.getClaim(field.getName()).as(fieldType);
        }
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, userBean, fieldValue);
    }

}
