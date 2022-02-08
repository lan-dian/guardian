package com.landao.guardian.core.handler;

import com.landao.guardian.annotations.system.Handler;
import com.landao.guardian.annotations.author.RequiredLogin;
import com.landao.guardian.core.GuardianContext;
import com.landao.guardian.exception.author.UnLoginException;
import com.landao.guardian.exception.system.GuardianAnnotationException;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

@Handler
public class LoginHandler {


    public void loginCheck(Method method) {
        RequiredLogin requiredLoginAnnotation = getRequiredLoginAnnotation(method);
        boolean login = GuardianContext.isLogin();
        if(login){//登陆
            checkIfLogin(requiredLoginAnnotation);
        }else {//未登陆
            checkIfUnLogin(requiredLoginAnnotation);
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
                String userType = GuardianContext.getUserType();
                if(onlyFor.length!=0){//设置了仅仅容许部分用户登陆
                    for (String allowed : onlyFor) {
                        if(Objects.equals(allowed,userType)){
                            return;
                        }
                    }
                    //没有在onlyFor找到我
                    throw new UnLoginException("仅容许"+ Arrays.toString(onlyFor)+"调用该接口");
                }else {
                    for (String ban : forbidden) {
                        if(Objects.equals(ban,userType)){
                            throw new UnLoginException(userType+"禁止访问该接口");
                        }
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
