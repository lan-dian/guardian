package com.landao.guardian.core;

import com.landao.guardian.annotations.system.Handler;
import com.landao.guardian.annotations.author.RequiredLogin;
import com.landao.guardian.exception.author.UnLoginException;
import com.landao.guardian.exception.system.GuardianAnnotationException;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Handler
public class LoginHandler {


    public void loginCheck(Method method) {
        RequiredLogin requiredLoginAnnotation = getRequiredLoginAnnotation(method);
        boolean login = ThreadStorage.isLogin();
        if(!login){//如果我们没有登陆
            checkIfUnLogin(requiredLoginAnnotation);
        }else {
            checkIfLogin(requiredLoginAnnotation);
        }
    }

    private void checkIfUnLogin(RequiredLogin requiredLoginAnnotation){
        if(requiredLoginAnnotation!=null){//如果需要登陆
            throw new UnLoginException();
        }
    }

    private void checkIfLogin(RequiredLogin requiredLoginAnnotation){
        if(requiredLoginAnnotation!=null){
            String[] onlyFor = requiredLoginAnnotation.onlyFor();
            String[] forbidden = requiredLoginAnnotation.forbidden();
            if(onlyFor.length!=0&&forbidden.length!=0){
                throw new GuardianAnnotationException("请不要在RequiredLogin同时指明onlyFor和forbidden这不符合逻辑");
            }
            if (onlyFor.length != 0 || forbidden.length != 0) {
                String userType = ThreadStorage.getUserType();
                if(onlyFor.length!=0){//设置了仅仅容许部分用户登陆
                    Optional<String> allowed = Arrays.stream(onlyFor)
                            .filter(E -> Objects.equals(E, userType))
                            .findAny();
                    if(allowed.isPresent()){
                        return;
                    }
                    //没有在onlyFor找到我
                    throw new UnLoginException("仅容许"+ Arrays.toString(onlyFor)+"调用该接口");
                }else {
                    Optional<String> ban = Arrays.stream(forbidden)
                            .filter(E->Objects.equals(E,userType))
                            .findAny();
                    if(ban.isPresent()){
                        throw new UnLoginException(userType+"禁止访问该接口");
                    }
                }
            } //两个长度都为0
            //所有人都需要登陆
        } //没有标注
        //可以通过
    }

    private RequiredLogin getRequiredLoginAnnotation(Method method) {
        RequiredLogin methodAnnotation = AnnotationUtils.findAnnotation(method, RequiredLogin.class);
        if (methodAnnotation!=null) {
            return methodAnnotation;
        }

        return AnnotationUtils.findAnnotation(method.getDeclaringClass(), RequiredLogin.class);
    }

}
