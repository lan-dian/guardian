package com.landao.guardian.core;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.landao.guardian.config.GuardianProperties;
import com.landao.guardian.annotations.system.GuardianService;
import com.landao.guardian.annotations.token.UserId;
import com.landao.guardian.consts.TokenConst;
import com.landao.guardian.core.context.CurrentSubject;
import com.landao.guardian.core.context.GuardianContext;
import com.landao.guardian.core.interfaces.BanDTO;
import com.landao.guardian.entity.model.DefaultBanDTO;
import com.landao.guardian.exception.token.TokenBeanException;
import com.landao.guardian.exception.token.TokenException;
import com.landao.guardian.util.JavaTypeUtil;
import com.landao.guardian.util.TokenUtil;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户认证服务
 * @param <T> tokenBean类型
 * @param <R> tokenBeanId类型
 */
public abstract class TokenService<T,R>{

    @Resource
    private GuardianProperties guardianProperties;


    public void setExtra(){
        GuardianContext.setExtra(null);
    }

    @SuppressWarnings("all")
    public <U> U getExtra(Class<U> type){
        return (U)CurrentSubject.getExtra();
    }

    @SuppressWarnings("unchecked")
    public T getTokenBean(){
        return (T) CurrentSubject.getUser();
    }

    public Set<String> getRoles(){
        return Collections.emptySet();
    }

    public Set<String> getPermissions(){
        return Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    public R getUserId(){
        return (R) CurrentSubject.getUserId();
    }

    public String getUserType() {
        return CurrentSubject.getUserType();
    }

    public BanDTO checkBan(){
        return new DefaultBanDTO();
    }

    public String parseToken(T userBean){
        JWTCreator.Builder builder = JWT.create();
        Class<?> userBeanClass = userBean.getClass();
        Field[] fields = userBeanClass.getDeclaredFields();
        boolean hasUserid=false;
        for (Field field : fields) {
            if(setClaim(field, userBean, builder)){
                if(hasUserid){
                    throw new TokenException("userId注解只能标注一个");
                }else {
                    hasUserid=true;
                }
            }
        }
        if(!hasUserid){
            throw new TokenException("请在类"+userBeanClass.getName()+"中至少唯一标注一个userId注解");
        }
        builder.withClaim(TokenConst.userType,getAnnotationUserType());
        return builder.sign(Algorithm.HMAC256(guardianProperties.getToken().getPrivateKey()));
    }

    //下面都是系统方法
    /*@Override
    public String generateBeanName(BeanDefinition beanDefinition, BeanDefinitionRegistry beanDefinitionRegistry) {
        GuardianService guardianService = AnnotationUtils.findAnnotation(this.getClass(), GuardianService.class);
        if(guardianService==null){
            throw new GuardianAnnotationException("请在"+this.getClass().getName()+"标注GuardService注解");
        }
        return guardianService.userType();
    }*/

    /**
     * 设置token属性并且检查是否含有 {@link UserId} 注解
     * @param field 属性字段
     * @param userBean 用户定义的token
     * @param builder jwt构建者
     * @return 如果含有UserId注解则返回true
     */
    private static boolean setClaim(Field field,Object userBean,JWTCreator.Builder builder){
        ReflectionUtils.makeAccessible(field);//设置可访问性
        //是否为可以支持的类型
        Class<?> fieldType = field.getType();
        if (!isClaimType(fieldType)){
            throw new TokenException("tokenBean不支持的类型:"+field.getType().getName());
        }
        String fieldName = field.getName();//获取字段的名称
        if(fieldName.startsWith("$")){
            throw new TokenBeanException("字段命名不合法:"+fieldName);
        }
        //获取字段的值
        Object fieldValue = ReflectionUtils.getField(field, userBean);
        boolean hasUserId=false;
        if(JavaTypeUtil.isInteger(fieldType)){
            Integer value= (Integer) fieldValue;
            if(field.isAnnotationPresent(UserId.class)){
                hasUserId=true;
                builder.withSubject(String.valueOf(value));
            }else {
                builder.withClaim(fieldName,value);
            }
        }else if(JavaTypeUtil.isString(fieldType)){
            String value=(String) fieldValue;
            if(field.isAnnotationPresent(UserId.class)){
                hasUserId=true;
                builder.withSubject(value);
            }else {
                builder.withClaim(fieldName,value);
            }
        }else if(JavaTypeUtil.isLong(fieldType)){
            Long value=(Long) fieldValue;
            if(field.isAnnotationPresent(UserId.class)){
                hasUserId=true;
                builder.withSubject(String.valueOf(value));
            }else {
                builder.withClaim(fieldName,value);
            }
        }else {
            throw new TokenException("不支持的类型:"+field.getType().getName());
        }
        return hasUserId;
    }

    private String getAnnotationUserType(){
        GuardianService guardianService = AnnotationUtils.findAnnotation(this.getClass(), GuardianService.class);
        if(guardianService ==null){
            throw new TokenException("请在认证类上标注@AuthorService注解");
        }
        //todo 支持别名
        String userType= guardianService.userType();
        if(!StringUtils.hasText(userType)){
            throw new TokenException("用户类型不能为空");
        }
        return userType.trim();
    }

    private static boolean isClaimType(Class<?> clazz){
        return JavaTypeUtil.isInteger(clazz) || JavaTypeUtil.isString(clazz) || JavaTypeUtil.isLong(clazz);
    }

    /**
     * 初始化用户所有信息
     */
    @SuppressWarnings("unchecked")
    void initUserInfo(String token,String privateKey){
        DecodedJWT decodedJwt = TokenUtil.getDecodedJwt(token,privateKey);
        Class<?> tokenBeanClass = JavaTypeUtil.getFirstGeneraType(this);
        T userBean=null;
        try {
            userBean = (T) tokenBeanClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new TokenBeanException(tokenBeanClass.getName()+"没有无参构造器");
        }

        Field[] fields = tokenBeanClass.getDeclaredFields();
        for (Field field : fields) {
            setField(field,userBean,decodedJwt);
        }
        CurrentSubject.setUser(userBean);
        CurrentSubject.login();
        CurrentSubject.setTokenService(this);
    }


    @SuppressWarnings("unchecked")
    private void setField(Field field, T userBean, DecodedJWT decoder){
        Class<?> fieldType = field.getType();
        Object fieldValue=null;
        if(field.isAnnotationPresent(UserId.class)){
            String subject = decoder.getSubject();
            R userId=null;
            if(JavaTypeUtil.isInteger(fieldType)){
                userId= (R) Integer.valueOf(subject);
            }else if(JavaTypeUtil.isLong(fieldType)){
                userId =(R) Long.valueOf(subject);
            }else if(JavaTypeUtil.isString(fieldType)){
                userId= (R) subject;
            }
            CurrentSubject.setUserId(userId);
            fieldValue=userId;
        }else {
            fieldValue=decoder.getClaim(field.getName()).as(fieldType);
        }
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field,userBean,fieldValue);
    }


}