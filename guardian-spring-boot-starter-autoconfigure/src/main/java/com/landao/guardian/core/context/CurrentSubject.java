package com.landao.guardian.core.context;


import com.landao.guardian.core.TokenService;
import com.landao.guardian.exception.author.UnLoginException;

import java.util.*;


public class CurrentSubject {

    private final static ThreadLocal<Object> user = new ThreadLocal<>();

    private final static ThreadLocal<String> userType=new ThreadLocal<>();

    private final static ThreadLocal<Object> userId=new ThreadLocal<>();

    private final static ThreadLocal<Boolean> login=ThreadLocal.withInitial(()->Boolean.FALSE);

    @SuppressWarnings("rawtypes")
    private final static ThreadLocal<TokenService> tokenService=new ThreadLocal<>();

    private final static ThreadLocal<Set<String>> roles=ThreadLocal.withInitial(Collections::emptySet);

    private final static ThreadLocal<Set<String>> permissions=ThreadLocal.withInitial(Collections::emptySet);

    private final static ThreadLocal<Object> extra=new ThreadLocal<>();

    public static void setExtra(Object obj){
        extra.set(obj);
    }

    public static Object getExtra(){
        return extra.get();
    }


    public static void setRoles(Collection<String> roles){
        CurrentSubject.roles.set(new HashSet<>(roles));
    }

    public static void setPermissions(Collection<String> permissions){
        CurrentSubject.permissions.set(new HashSet<>(permissions));
    }

    public static Set<String> getRoles(){
        checkLogin();
        return roles.get();
    }

    public static Set<String> getPermissions(){
        checkLogin();
        return permissions.get();
    }


    @SuppressWarnings("rawtypes")
    public static void setTokenService(TokenService tokenService){
        CurrentSubject.tokenService.set(tokenService);
    }

    @SuppressWarnings("rawtypes")
    public static TokenService getTokenService(){
        checkLogin();
        return tokenService.get();
    }

    public  static boolean isLogin(){
        return CurrentSubject.login.get();
    }

    public static void login(){
        login.set(Boolean.TRUE);
    }

    public static void outLogin(){
        login.set(Boolean.FALSE);
    }

    public static Object getUser() {
        checkLogin();
        return user.get();
    }

    public static String getUserType() {
        checkLogin();
        return userType.get();
    }

    public static Object getUserId() {
        checkLogin();
        return userId.get();
    }

    public static void setUser(Object user){
        CurrentSubject.user.set(user);
    }

    public static void setUserType(String userType){
        CurrentSubject.userType.set(userType);
    }

    public static void setUserId(Object userId){
        CurrentSubject.userId.set(userId);
    }

    private static void checkLogin(){
        if(!isLogin()){
            throw new UnLoginException();
        }
    }

    public static void clearAll(){
        user.remove();
        userType.remove();
        userId.remove();
        login.remove();
        tokenService.remove();
        roles.remove();
        permissions.remove();
        extra.remove();
    }

}
