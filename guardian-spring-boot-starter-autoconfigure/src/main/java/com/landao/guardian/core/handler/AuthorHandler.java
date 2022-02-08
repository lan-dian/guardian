package com.landao.guardian.core.handler;

import com.landao.guardian.annotations.author.RequiredPermission;
import com.landao.guardian.annotations.author.RequiredRole;
import com.landao.guardian.annotations.author.RequiredRoles;
import com.landao.guardian.annotations.system.Handler;
import com.landao.guardian.core.GuardianContext;
import com.landao.guardian.entity.enums.LogicType;
import com.landao.guardian.exception.author.UnAuthorizationException;
import com.landao.guardian.exception.author.UnLoginException;
import com.landao.guardian.exception.system.GuardianAnnotationException;
import com.landao.guardian.exception.system.GuardianException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Handler
public class AuthorHandler {

    @Resource
    private ApplicationContext applicationContext;


    public void checkAuthor(Method method){
        boolean login = GuardianContext.isLogin();
        if(login){//登陆
            String userType = GuardianContext.getUserType();
            checkIfLogin(method,userType);
        }else {//未登陆
            checkIfUnLogin(method);
        }
    }

    @SuppressWarnings("all")
    private void checkIfLogin(Method method,String userType){
        boolean keepOn = checkRequiredPermission(method);
        if(keepOn){
            checkRequiredRoles(method,userType);
        }
    }

    private void checkRequiredRoles(Method method,String userType){
        RequiredRole requiredRoleAnnotation = findRequiredRoleAnnotation(method, userType);
        if(requiredRoleAnnotation==null){
            return;
        }
        LogicType logicType = requiredRoleAnnotation.logicType();
        Set<String> userRoles = GuardianContext.getRoles();
        if(userRoles.isEmpty()){
            throw new UnAuthorizationException("该用户未包含任何角色");
        }
        String[] roles = requiredRoleAnnotation.roles();
        if(roles.length==0){
            throw new UnAuthorizationException("请在RequiredRole中至少指定一个角色");
        }
        if(logicType==LogicType.Or){
            checkRoleOr(userRoles,roles);
        }else if(logicType==LogicType.And){
            if(roles.length==1){
                throw new UnAuthorizationException("RequiredRole的角色判断逻辑为And,请指定两个及以上的角色");
            }
            checkRoleAnd(userRoles,roles);
        }else if(logicType==LogicType.Not){
            checkRoleNot(userRoles,roles);
        }else {
            throw new GuardianException("未知的逻辑类型"+logicType);
        }
    }

    /**
     * 检测是否通过权限验证
     * @return 是否继续坚持角色注解
     */
    private boolean checkRequiredPermission(Method method){
        RequiredPermission requiredPermission = AnnotationUtils.findAnnotation(method, RequiredPermission.class);
        if(requiredPermission==null){
            return true;
        }
        //获取类型
        LogicType logicType = requiredPermission.logicType();
        if(logicType==LogicType.Not){
            throw new GuardianAnnotationException("你不能在RequiredPermission使用Not,因为这不符合逻辑");
        }
        String[] permissions = requiredPermission.permissions();
        if(permissions.length==0){
            throw new GuardianAnnotationException("RequiredPermission的permissions至少指明一个权限表达式");
        }
        Set<String> userPermissions = GuardianContext.getPermissions();
        if(userPermissions.isEmpty()){
            //如果用户没有任何特殊的权限，那么它不可能被禁止,也不可能通过任何权限
            return true;//继续查询角色是否满足
        }
        if(logicType==LogicType.And){
            return checkPermissionAnd(userPermissions,permissions);
        }else if(logicType==LogicType.Or){
            return checkPermissionOr(userPermissions,permissions);
        }else {
            throw new GuardianException("未知的逻辑枚举"+logicType);
        }
    }
    
    private boolean checkPermissionAnd(Set<String> userPermissions,String[] permissions){
        for (String permission : permissions) {//遍历检测
            if(!StringUtils.hasText(permission)){
                throw new GuardianAnnotationException("权限表达式不能是空字符串");
            }
            if(permission.contains("*")){
                throw new GuardianAnnotationException("permission中不能包含*");
            }
            String[] split = permission.split(" ");
            if(split.length!=2){
                throw new GuardianAnnotationException("权限应该格式应该为{资源}:{操作}");
            }
            String resource=split[0];
            String operate=split[0];
            for (String userPermission : userPermissions) {
                //如果发现禁封,直接弹出异常就可以了
                if(userPermission.startsWith("!")){
                    if(userPermission.endsWith("*")){
                        if(Objects.equals(userPermission.substring(1,userPermission.length()-3),resource)){
                            //被禁止访问
                            throw new UnAuthorizationException("你被禁止关于"+resource+"的所有操作");
                        }
                    }else {
                        if(Objects.equals(userPermission.substring(1),permission)){
                            throw new UnAuthorizationException("你被禁止对"+resource+"进行"+operate+"操作");
                        }
                    }
                }
                //是否匹配
                if(userPermission.endsWith("*")){
                    if(Objects.equals(userPermission.substring(0,userPermission.length()-3),resource)){
                        break;//后面的就不看
                    }
                }else {
                    if(Objects.equals(userPermission,permission)){
                        break;//同上
                    }
                }
            }
            throw new UnAuthorizationException("你不具有对"+resource+"进行"+operate+"操作的权限");
        }//如果都通过了，说明没有匹配，也没有禁止,需要继续检查
        return true;
    }

    //一切判断的目的是让它尽快的通过
    private boolean checkPermissionOr(Set<String> userPermissions,String[] permissions){
        for (String userPermission : userPermissions) {//应该有一个排序,把!丢掉,然后把*放在前面,不过数量少，排序没有必要
            if(userPermission.startsWith("!")){
                continue;
            }
            if(userPermission.endsWith("*")){
                for (String permission : permissions) {
                    int index = permission.indexOf(":");
                    String source = permission.substring(0, index - 1);
                    if(Objects.equals(userPermission.substring(0,userPermission.length()-3),source)){
                        return false;
                    }
                }
            }else {
                for (String permission : permissions) {
                    if(Objects.equals(userPermission,permission)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void checkRoleOr(Set<String> userRoles,String[] roles){
        for (String requested : roles) {
            if(userRoles.contains(requested)){
                return;
            }
        }
        throw new UnAuthorizationException("你必须至少拥有"+ Arrays.toString(roles)+"中的一个角色");
    }

    private void checkRoleAnd(Set<String> userRoles,String[] roles){
        Set<String> requireAll = Arrays.stream(roles).collect(Collectors.toSet());
        if (!userRoles.containsAll(requireAll)) {
            throw new UnAuthorizationException("你必须同时拥有"+requireAll+"这些角色");
        }
    }

    private void checkRoleNot(Set<String> userRoles,String[] roles){
        Set<String> forbiddings = Arrays.stream(roles).collect(Collectors.toSet());
        if(forbiddings.containsAll(userRoles)){
            throw new UnAuthorizationException(forbiddings+"禁止访问该接口");
        }
    }
    
    private RequiredRole findRequiredRoleAnnotation(Method method, String userType){
        RequiredRoles methodRoles = AnnotationUtils.findAnnotation(method, RequiredRoles.class);
        if (methodRoles!=null) {
            for (RequiredRole requiredRole : methodRoles.value()) {
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
            for (RequiredRole requiredRole : classRoles.value()) {
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

    private RequiredPermission findRequiredPermissionAnnotation(Method method){
        RequiredPermission methodPermission = AnnotationUtils.findAnnotation(method, RequiredPermission.class);
        if (methodPermission!=null) {
            return methodPermission;
        }
        RequiredPermission classPermission = AnnotationUtils.findAnnotation(method.getDeclaringClass(), RequiredPermission.class);
        if(classPermission!=null){
            return classPermission;
        }
        return null;
    }

    private boolean isUserTypeEqual(RequiredRole requiredRole,String userType){
        return Objects.equals(requiredRole.userType(),userType);
    }


    private void checkIfUnLogin(Method method){
        if(hasAnyAuthorAnnotation(method)){
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


    private boolean hasAnyRequiredPermissionAnnotation(Method method){
        return findRequiredPermissionAnnotation(method) != null;
    }

    private boolean hasAnyAuthorAnnotation(Method method){
        return hasAnyRequiredPermissionAnnotation(method) || hasAnyRequiredRoleAnnotation(method);
    }


}
