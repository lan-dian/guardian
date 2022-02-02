package com.landao.guardian.core;

import com.landao.guardian.annotations.author.RequiredRole;
import com.landao.guardian.annotations.author.RequiredRoles;
import com.landao.guardian.annotations.system.Handler;
import com.landao.guardian.core.context.CurrentSubject;
import com.landao.guardian.enums.LogicType;
import com.landao.guardian.exception.author.AuthorizationException;
import com.landao.guardian.exception.author.UnLoginException;
import com.landao.guardian.exception.system.GuardianException;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Handler
public class RoleHandler {


    public void checkRole(Method method){
        boolean login = CurrentSubject.isLogin();
        if(login){//登陆
            String userType = CurrentSubject.getUserType();
            checkIfLogin(method,userType);
        }else {//未登陆
            checkIfUnLogin(method);
        }
    }

    @SuppressWarnings("all")
    private void checkIfLogin(Method method,String userType){
        RequiredRole requiredRoleAnnotation = findRequiredRoleAnnotation(method, userType);
        if(requiredRoleAnnotation==null){
            throw new AuthorizationException(userType+"无权访问该接口");
        }
        LogicType logicType = requiredRoleAnnotation.logicType();
        Set<String> userRoles = CurrentSubject.getRoles();
        String[] roles = requiredRoleAnnotation.roles();
        if(roles.length==0){
            throw new AuthorizationException("请在RequiredRole中至少指定一个角色");
        }
        if(logicType==LogicType.Or){
            checkRoleOr(userRoles,roles);
        }else if(logicType==LogicType.And){
            if(roles.length==1){
                throw new AuthorizationException("RequiredRole的角色判断逻辑为And,请指定两个及以上的角色");
            }
            checkRoleAnd(userRoles,roles);
        }else if(logicType==LogicType.Not){
            checkRoleNot(userRoles,roles);
        }else {
            throw new GuardianException("未知的逻辑类型"+logicType);
        }
    }

    private void checkRoleOr(Set<String> userRoles,String[] roles){
        for (String requested : roles) {
            if(userRoles.contains(requested)){
                return;
            }
        }
        throw new AuthorizationException("你必须至少拥有"+ Arrays.toString(roles)+"中的一个角色");
    }

    private void checkRoleAnd(Set<String> userRoles,String[] roles){
        Set<String> requireAll = Arrays.stream(roles).collect(Collectors.toSet());
        if (!userRoles.containsAll(requireAll)) {
            throw new AuthorizationException("你必须同时拥有"+requireAll+"这些角色");
        }
    }

    private void checkRoleNot(Set<String> userRoles,String[] roles){
        Set<String> forbiddings = Arrays.stream(roles).collect(Collectors.toSet());
        if(forbiddings.containsAll(userRoles)){
            throw new AuthorizationException(forbiddings+"禁止访问该接口");
        }
    }


    private RequiredRole findRequiredRoleAnnotation(Method method, String userType){
        RequiredRoles methodRoles = AnnotationUtils.findAnnotation(method, RequiredRoles.class);
        if (methodRoles!=null) {
            for (RequiredRole requiredRole : methodRoles.requiredRoles()) {
                if(isUserTypeEqual(requiredRole,userType)){
                    return requiredRole;
                }
            }
        }
        RequiredRole methodRole = AnnotationUtils.findAnnotation(method, RequiredRole.class);
        if(methodRole!=null){
            if(isUserTypeEqual(methodRole,userType)){
                return methodRole;
            }
        }
        //找类上的
        Class<?> declaringClass = method.getDeclaringClass();
        RequiredRoles classRoles = AnnotationUtils.findAnnotation(declaringClass, RequiredRoles.class);
        if (classRoles!=null) {
            for (RequiredRole requiredRole : classRoles.requiredRoles()) {
                if(isUserTypeEqual(requiredRole,userType)){
                    return requiredRole;
                }
            }
        }
        RequiredRole classRole = AnnotationUtils.findAnnotation(declaringClass, RequiredRole.class);
        if(classRole!=null){
            if(isUserTypeEqual(classRole,userType)){
                return classRole;
            }
        }
        return null;
    }

    private boolean isUserTypeEqual(RequiredRole requiredRole,String userType){
        return Objects.equals(requiredRole.userType(),userType);
    }

    private void checkIfUnLogin(Method method){
        boolean hasRequiredRoleAnnotation = hasAnyRequiredRoleAnnotation(method);
        if(hasRequiredRoleAnnotation){
            //注意这里抛出的是UnLoginException异常
            throw new UnLoginException();
        }
    }

    private boolean hasAnyRequiredRoleAnnotation(Method method){
        RequiredRoles methodRoles = AnnotationUtils.findAnnotation(method, RequiredRoles.class);
        if (methodRoles!=null) {
            return true;
        }
        RequiredRole methodRole = AnnotationUtils.findAnnotation(method, RequiredRole.class);
        if(methodRole!=null){
            return true;
        }
        //找类上的
        Class<?> declaringClass = method.getDeclaringClass();
        RequiredRoles classRoles = AnnotationUtils.findAnnotation(declaringClass, RequiredRoles.class);
        if (classRoles!=null) {
            return true;
        }
        RequiredRole classRole = AnnotationUtils.findAnnotation(declaringClass, RequiredRole.class);
        if(classRole!=null){
            return true;
        }
        return false;
    }



}
