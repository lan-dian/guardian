package com.landao.guardian.core.context;


import com.landao.guardian.core.TokenService;
import com.landao.guardian.exception.author.UnLoginException;
import com.landao.guardian.util.GuardianUtil;

import java.util.*;


public class CurrentSubject {

    private final static ThreadLocal<Object> user = new ThreadLocal<>();

    private final static ThreadLocal<String> userType=new ThreadLocal<>();

    private final static ThreadLocal<Object> userId=new ThreadLocal<>();

    private final static ThreadLocal<Boolean> login=ThreadLocal.withInitial(()->Boolean.FALSE);

    @SuppressWarnings("rawtypes")
    private final static ThreadLocal<TokenService> tokenService=new ThreadLocal<>();

    private final static ThreadLocal<Set<String>> roles=new ThreadLocal<>();

    private final static ThreadLocal<Set<String>> permissions=new ThreadLocal<>();

    private final static ThreadLocal<Object> extra=new ThreadLocal<>();

    public static void setExtra(Object obj){
        extra.set(obj);
    }

    public static Object getExtra(){
        return extra.get();
    }

    @SuppressWarnings("all")
    public static Set<String> getRoles(){
        checkLogin();
        if(roles.get()==null){
            synchronized (CurrentSubject.class){
                if(roles.get()==null){
                    Set<String> roles = tokenService.get().getRoles();
                    CurrentSubject.roles.set(GuardianUtil.toLowerCase(roles));
                }
            }
        }
        return roles.get();
    }

    @SuppressWarnings("all")
    public static Set<String> getPermissions(){
        checkLogin();
        if(permissions.get()==null){
            synchronized (CurrentSubject.class){
                if(permissions.get()==null){
                    Set<String> permissions = tokenService.get().getPermissions();
                    CurrentSubject.permissions.set(GuardianUtil.toLowerCase(permissions));
                }
            }
        }
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
