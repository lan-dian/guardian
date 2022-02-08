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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.lang.reflect.Field;


@Handler
public class TokenHandler{

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private RedisTemplate<String, Object> redis;


    public void initTokenBean(String token, String privateKey){
        DecodedJWT decodedJwt = TokenUtils.getDecodedJwt(token, privateKey);

        GuardianContext.setDecodedJWT(decodedJwt);

        String userType = decodedJwt.getSubject();

        Object bean = applicationContext.getBean(userType);
        if(!(bean instanceof TokenService)){
            throw new GuardianAnnotationException("用户类型为"+userType+"的认证服务没有继承tokenService");
        }
        TokenService<?,?> tokenService=(TokenService<?,?>)bean;

        Class<?> tokenBeanType = TypeUtils.getFirstGeneraType(tokenService);

        initUserInfo(decodedJwt,tokenBeanType);

        GuardianContext.setUserType(userType);
        GuardianContext.login();
        GuardianContext.setTokenService(tokenService);

        Long expiredTime = (Long)RedisUtils.value.get(GuardianConst.redisPrefix + ":"
                + userType + ":"
                + GuardianContext.getUserId());
        if(expiredTime!=null){
            long publishTime = GuardianContext.getPublishTime();
            if(expiredTime.compareTo(publishTime)>0){
                throw new UnLoginException("该token已失效");
            }
        }
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
