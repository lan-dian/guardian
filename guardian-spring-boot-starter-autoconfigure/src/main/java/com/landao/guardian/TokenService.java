package com.landao.guardian;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.landao.guardian.annotations.AuthorService;
import com.landao.guardian.annotations.UserId;
import com.landao.guardian.consts.TokenConst;
import com.landao.guardian.util.JavaTypeUtil;
import com.landao.guardian.util.NewxWebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * 用户认证服务
 * @param <T> 用户类型
 */
public abstract class TokenService<T> {

    //todo 增多支持的的类型
    @Resource
    private GuardianProperties guardianProperties;

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
            throw new TokenException("请至少在类中标注一个userId注解");
        }
        builder.withClaim(TokenConst.userTypePrefix,getAnnotationUserType());
        return builder.sign(Algorithm.HMAC256(guardianProperties.getToken().getPrivateKey()));
    }

    /**
     * 设置token属性并且检查是否含有 {@link com.landao.guardian.annotations.UserId} 注解
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
            throw new TokenException("token不支持的类型:"+field.getType().getName());
        }
        String fieldName = field.getName();//获取字段的名称
        if(fieldName.startsWith("$")){
            throw new TokenException("不规范的属性命名:"+fieldName);
        }
        //获取字段的值
        Object fieldValue = ReflectionUtils.getField(field, userBean);
        boolean hasUserId=false;
        if(JavaTypeUtil.isInteger(fieldType)){
            Integer value= (Integer) fieldValue;
            if(field.isAnnotationPresent(UserId.class)){
                hasUserId=true;
                builder.withClaim(TokenConst.userIdPrefix,value);
            }else {
                builder.withClaim(fieldName,value);
            }
        }else if(JavaTypeUtil.isString(fieldType)){
            String value=(String) fieldValue;
            if(field.isAnnotationPresent(UserId.class)){
                hasUserId=true;
                builder.withClaim(TokenConst.userIdPrefix,value);
            }else {
                builder.withClaim(fieldName,value);
            }
        }else if(JavaTypeUtil.isLong(fieldType)){
            Long value=(Long) fieldValue;
            if(field.isAnnotationPresent(UserId.class)){
                hasUserId=true;
                builder.withClaim(TokenConst.userIdPrefix,value);
            }else {
                builder.withClaim(fieldName,value);
            }
        }else {
            throw new TokenException("不支持的类型:"+field.getType().getName());
        }
        return hasUserId;
    }

    private String getAnnotationUserType(){
        AuthorService authorService = AnnotationUtils.findAnnotation(this.getClass(), AuthorService.class);
        if(authorService==null){
            throw new TokenException("请在认证类上标注@AuthorService注解");
        }
        //todo 支持别名
        String userType=authorService.userType();
        if(!StringUtils.hasText(userType)){
            throw new TokenException("用户类型不能为空");
        }
        return userType.trim();
    }

    private static boolean isClaimType(Class<?> clazz){
        return JavaTypeUtil.isInteger(clazz) || JavaTypeUtil.isString(clazz) || JavaTypeUtil.isLong(clazz);
    }

    @SuppressWarnings("unchecked")
    public T getTokenBean(){
        return  (T) NewxWebUtil.getAttribute(TokenConst.requestAttributePrefix + "user");
    }

    public abstract Set<String> getRoles();

    public abstract Set<String> getPermissions();

    /**
     * 调用专为子类使用的{@link TokenService#getUserId(Class)}定义自己的id类型
     */
    public abstract Object getUserId();

    @SuppressWarnings("unchecked")
    protected <R> R getUserId(Class<R> tClass){
        return  (R) NewxWebUtil.getAttribute(TokenConst.userIdPrefix);
    }

    public String getUserType() {
        return (String) NewxWebUtil.getAttribute(TokenConst.requestAttributePrefix + "userType");
    }

    public static class TokenException extends RuntimeException{

        public TokenException(String message) {
            super(message);
        }

    }

}