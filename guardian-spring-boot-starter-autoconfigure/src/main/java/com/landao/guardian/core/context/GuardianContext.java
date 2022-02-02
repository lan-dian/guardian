package com.landao.guardian.core.context;


import com.landao.guardian.core.TokenService;

import java.rmi.NotBoundException;
import java.util.Set;

/**
 * 系统应用上下文,可以获取当前用户的所有信息
 */
public class GuardianContext {

    public static Object getUserId(){
        return CurrentSubject.getUserId();
    }

    @SuppressWarnings("all")
    public static <T> T getUserId(Class<T> type){
        return (T) getUserId();
    }

    public static Object getUser(){
        return CurrentSubject.getUser();
    }

    @SuppressWarnings("all")
    public static <T> T getUser(Class<T> type){
        return (T) getUser();
    }

    public static String getUserType(){
        return CurrentSubject.getUserType();
    }

    public static Set<String> getRoles(){
        return CurrentSubject.getRoles();
    }

    public static boolean containsRole(String role){
        return getRoles().contains(role);
    }

    public static boolean containsRole(Enum<?> userRole){
        return getRoles().contains(userRole.name());
    }

    public static Set<String> getPermissions(){
        return CurrentSubject.getPermissions();
    }

    //todo 补充permissions 需要模糊匹配

    public static boolean isLogin(){
        return CurrentSubject.isLogin();
    }

    public static TokenService<?,?> getTokenService(){
        return CurrentSubject.getTokenService();
    }

    @SuppressWarnings("all")
    public static <T,R> TokenService<T,R> getTokenService(Class<T> tokenBeanType,Class<R> userIdType){
        return (TokenService<T,R>)CurrentSubject.getTokenService();
    }



}
