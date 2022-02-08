package com.landao.guardian.core;


import com.landao.guardian.exception.author.UnLoginException;

import java.util.*;
import java.util.stream.Collectors;


public class GuardianContext {

    private GuardianContext(){

    }

    private static final ThreadLocal<GuardianContext> GUARDIAN_HOLDER=new ThreadLocal<>();

    private Object user;

    private String userType;

    private Object userId;

    private Boolean login;

    private TokenService<?,?> tokenService;

    private Set<String> roles;

    private Set<String> permissions;

    private Object extra;

    public static void clear(){
        GUARDIAN_HOLDER.remove();
    }

    private static GuardianContext getCurrent(){
        GuardianContext guardianContext = GUARDIAN_HOLDER.get();
        if(guardianContext ==null){
            guardianContext = new GuardianContext();
            guardianContext.login=false;
            guardianContext.roles=Collections.emptySet();
            guardianContext.permissions=Collections.emptySet();
            GUARDIAN_HOLDER.set(guardianContext);
        }
        return guardianContext;
    }

    public static boolean isLogin(){
        return getCurrent().login;
    }

    private static void checkLogin(){
        if(!isLogin()){
            throw new UnLoginException();
        }
    }

    public static Object getUser() {
        checkLogin();
        return getCurrent().user;
    }

    @SuppressWarnings("all")
    public static <T> T getUser(Class<T> type){
        return (T)getUser();
    }

    public static void setUser(Object user) {
        getCurrent().user=user;
    }

    public static String getUserType() {
        checkLogin();
        return getCurrent().userType;
    }

    public static void setUserType(String userType) {
        getCurrent().userType = userType;
    }

    public static Object getUserId() {
        checkLogin();
        return getCurrent().userId;
    }

    public static void setUserId(Object userId) {
        getCurrent().userId = userId;
    }

    public static void login() {
        getCurrent().login=true;
    }

    public static void logout(){
        getCurrent().login=false;
    }

    public static TokenService<?, ?> getTokenService() {
        checkLogin();
        return getCurrent().tokenService;
    }

    public static void setTokenService(TokenService<?, ?> tokenService) {
        getCurrent().tokenService = tokenService;
    }

    public static Set<String> getRoles() {
        checkLogin();
        Set<String> roles = getCurrent().roles;
        if(roles.isEmpty()){
            synchronized (GuardianContext.class){
                if(roles.isEmpty()){
                    roles=getTokenService().getRoles().stream().map(String::toLowerCase).collect(Collectors.toSet());
                    getCurrent().roles=roles;
                }
            }
        }
        return roles;
    }

    public static Set<String> getPermissions() {
        checkLogin();
        Set<String> permissions = getCurrent().permissions;
        if(permissions.isEmpty()){
            synchronized (GuardianContext.class){
                if(permissions.isEmpty()){
                    permissions=getTokenService().getPermissions().stream().map(String::toLowerCase).collect(Collectors.toSet());
                    getCurrent().permissions=permissions;
                }
            }
        }
        return permissions;
    }

    public static Object getExtra() {
        checkLogin();
        return getCurrent().extra;
    }

    public static void setExtra(Object extra) {
        getCurrent().extra=extra;
    }

}
