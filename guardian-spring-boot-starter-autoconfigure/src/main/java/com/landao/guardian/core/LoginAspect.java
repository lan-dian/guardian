package com.landao.guardian.core;


import com.landao.guardian.annotations.RequiredLogin;
import com.landao.guardian.exception.author.UnLoginException;
import com.landao.guardian.exception.system.GuardianAnnotationException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Objects;


@Aspect
@Order(1)
public class LoginAspect {


    @SuppressWarnings("rawtypes")
    private final ThreadStorage threadStorage = new ThreadStorage();

    public static final String or = "||";

    @Pointcut("@within(com.landao.guardian.annotations.RequiredRole)"
            + or + "@annotation(com.landao.guardian.annotations.RequiredRole)")
    public void pointCut() {

    }

    @SuppressWarnings("rawtypes")
    @Before(value = "pointCut()")
    public void logBeforeController(JoinPoint joinPoint) {
        if(!canPass(joinPoint)){
            throw new UnLoginException();
        }
    }

    private boolean canPass(JoinPoint joinPoint){
        RequiredLogin requiredLoginAnnotation = getRequiredLoginAnnotation(joinPoint);
        boolean login = threadStorage.isLogin();
        if (!login) {//如果我们没有登陆
            if(requiredLoginAnnotation!=null){//如果需要登陆
                return false;
            }else {
                return true;
            }
        }else {//如果我登陆了
            if(requiredLoginAnnotation!=null){
                String[] onlyFor = requiredLoginAnnotation.onlyFor();
                String[] forbidden = requiredLoginAnnotation.forbidden();
                if(onlyFor.length==0 && forbidden.length==0){//所有人都需要登陆
                    //这种情况和用户类型无关，我登陆了，所以可以通过
                    return true;
                }else if(onlyFor.length!=0){//设置了仅仅容许部分用户登陆
                    String userType = threadStorage.getUserType();
                    for (String allowUserType : onlyFor) {
                        if(Objects.equals(userType, allowUserType)){
                            return true;
                        }
                    }
                    //没有在onlyFor找到我
                    return false;
                }else if (forbidden.length!=0){
                    String userType = threadStorage.getUserType();
                    for (String forbiddenUserType : forbidden) {
                        if(Objects.equals(userType,forbiddenUserType)){
                            return false;
                        }
                    }
                    return true;
                }else {
                    throw new GuardianAnnotationException("请不要为RequireLogin设置onlyFor和forbidden");
                }
            }else {//没有标注,所以可以通过
                return true;
            }
        }
    }


    private RequiredLogin getRequiredLoginAnnotation(JoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        RequiredLogin methodAnnotation = AnnotationUtils.findAnnotation(signature.getMethod(), RequiredLogin.class);
        if (methodAnnotation!=null) {
            return methodAnnotation;
        }

        RequiredLogin typeAnnotation = AnnotationUtils.findAnnotation(signature.getDeclaringType(), RequiredLogin.class);
        if(typeAnnotation!=null){
            return typeAnnotation;
        }
        return null;
    }




}
