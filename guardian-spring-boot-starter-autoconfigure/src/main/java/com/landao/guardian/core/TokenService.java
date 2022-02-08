package com.landao.guardian.core;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.landao.guardian.annotations.token.UserId;
import com.landao.guardian.config.GuardianProperties;
import com.landao.guardian.consts.TokenConst;
import com.landao.guardian.core.interfaces.BanDTO;
import com.landao.guardian.entity.model.DefaultBanDTO;
import com.landao.guardian.exception.token.TokenBeanException;
import com.landao.guardian.exception.token.TokenException;
import com.landao.guardian.util.JavaTypeUtil;
import com.landao.guardian.util.TokenUtil;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;

/**
 * 用户认证服务
 *
 * @param <T> tokenBean类型
 * @param <R> tokenBeanId类型
 */
public class TokenService<T, R> implements BeanNameAware{

    @Resource
    private GuardianProperties guardianProperties;

    private String userType;

    public void setExtra() {
        GuardianContext.setExtra(null);
    }

    @SuppressWarnings("all")
    public <U> U getExtra(Class<U> type) {
        return (U) GuardianContext.getExtra();
    }

    @SuppressWarnings("unchecked")
    public T getTokenBean() {
        return (T) GuardianContext.getUser();
    }

    public Set<String> getRoles() {
        return Collections.emptySet();
    }

    public Set<String> getPermissions() {
        return Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    public R getUserId() {
        return (R) GuardianContext.getUserId();
    }

    public String getUserType() {
        return GuardianContext.getUserType();
    }

    public BanDTO checkBan() {
        return new DefaultBanDTO();
    }

    public String parseToken(T userBean) {
        JWTCreator.Builder builder = JWT.create();
        Class<?> userBeanClass = userBean.getClass();
        Field[] fields = userBeanClass.getDeclaredFields();
        boolean hasUserid = false;
        for (Field field : fields) {
            if (setClaim(field, userBean, builder)) {
                if (hasUserid) {
                    throw new TokenException("userId注解只能标注一个");
                } else {
                    hasUserid = true;
                }
            }
        }
        if (!hasUserid) {
            throw new TokenException("请在类" + userBeanClass.getName() + "中至少唯一标注一个userId注解");
        }
        builder.withClaim(TokenConst.userType, userType);
        return builder.sign(Algorithm.HMAC256(guardianProperties.getToken().getPrivateKey()));
    }

    /*
     * 下面都是系统方法
     */

    /**
     * 设置token属性并且检查是否含有 {@link UserId} 注解
     *
     * @param field    属性字段
     * @param userBean 用户定义的token
     * @param builder  jwt构建者
     * @return 如果含有UserId注解则返回true
     */
    private static boolean setClaim(Field field, Object userBean, JWTCreator.Builder builder) {
        ReflectionUtils.makeAccessible(field);//设置可访问性
        //是否为可以支持的类型
        Class<?> fieldType = field.getType();
        if (!isClaimType(fieldType)) {
            throw new TokenException("tokenBean不支持的类型:" + field.getType().getName());
        }
        String fieldName = field.getName();//获取字段的名称
        if (fieldName.startsWith("$")) {
            throw new TokenBeanException("字段命名不合法:" + fieldName);
        }
        //获取字段的值
        Object fieldValue = ReflectionUtils.getField(field, userBean);
        boolean hasUserId = false;
        if (JavaTypeUtil.isInteger(fieldType)) {
            Integer value = (Integer) fieldValue;
            if (field.isAnnotationPresent(UserId.class)) {
                hasUserId = true;
                builder.withSubject(String.valueOf(value));
            } else {
                builder.withClaim(fieldName, value);
            }
        } else if (JavaTypeUtil.isString(fieldType)) {
            String value = (String) fieldValue;
            if (field.isAnnotationPresent(UserId.class)) {
                hasUserId = true;
                builder.withSubject(value);
            } else {
                builder.withClaim(fieldName, value);
            }
        } else if (JavaTypeUtil.isLong(fieldType)) {
            Long value = (Long) fieldValue;
            if (field.isAnnotationPresent(UserId.class)) {
                hasUserId = true;
                builder.withSubject(String.valueOf(value));
            } else {
                builder.withClaim(fieldName, value);
            }
        } else {
            throw new TokenException("不支持的类型:" + field.getType().getName());
        }
        return hasUserId;
    }

    private static boolean isClaimType(Class<?> clazz) {
        return JavaTypeUtil.isInteger(clazz) || JavaTypeUtil.isString(clazz) || JavaTypeUtil.isLong(clazz);
    }

    @Override
    public void setBeanName(String name) {
        this.userType=name;
    }

}