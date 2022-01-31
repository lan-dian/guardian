package com.landao.guardian.aspect;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.landao.guardian.TokenService;
import com.landao.guardian.annotations.AuthorService;
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

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;


@Aspect
@Component
@Order(0)
public class TokenAspect {

    @Resource
    ApplicationContext applicationContext;

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void pointCut() {

    }

    @Before(value = "pointCut()")
    public void logBeforeController(JoinPoint joinPoint) {
        Map<String, Object> tokenServices = applicationContext.getBeansWithAnnotation(AuthorService.class);
        if(TokenUtil.hasToken()){
            String userType = TokenUtil.getUserType();
            Collection<Object> services = tokenServices.values();
            for (Object service : services) {
                AuthorService authorService = AnnotationUtils.findAnnotation(service.getClass(), AuthorService.class);
                if(Objects.equals(userType,authorService.userType())){
                    Class<?> tokenBeanType = JavaTypeUtil.getFirstGeneraType(service);
                    initRequestAttributes(TokenUtil.getToken(),tokenBeanType);
                }
            }
        }
    }

    public <T> void initRequestAttributes(String authorization,Class<T> tClass){
        authorization= TokenUtil.verifyToken(authorization);
        DecodedJWT decodedJWT = TokenUtil.getDecodedJwt(authorization);

        T userBean=null;
        try {
            userBean =  tClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new TokenService.TokenException(tClass.getName()+"没有无参构造器");
        }
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            setField(field,userBean,decodedJWT);
        }

        NewxWebUtil.setAttribute(TokenConst.requestAttributePrefix+"user",userBean);
        String userType = decodedJWT.getClaim(TokenConst.userTypePrefix).asString();
        NewxWebUtil.setAttribute(TokenConst.requestAttributePrefix+"userType",userType);
    }

    private static <T> void setField(Field field,T userBean,DecodedJWT decoder){
        Class<?> fieldType = field.getType();
        Object fieldValue=null;
        if(field.isAnnotationPresent(UserId.class)){
            fieldValue = decoder.getClaim(TokenConst.userIdPrefix).as(fieldType);
            NewxWebUtil.setAttribute(TokenConst.userIdPrefix,fieldValue);
        }else {
            fieldValue=decoder.getClaim(field.getName()).as(fieldType);
        }
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field,userBean,fieldValue);
    }



}
