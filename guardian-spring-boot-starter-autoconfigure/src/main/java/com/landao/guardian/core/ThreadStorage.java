package com.landao.guardian.core;


import com.landao.guardian.exception.author.UnLoginException;


public class ThreadStorage {

    private final static ThreadLocal<Object> user = new ThreadLocal<>();

    private final static ThreadLocal<String> userType=new ThreadLocal<>();

    private final static ThreadLocal<Object> userId=new ThreadLocal<>();

    private final static ThreadLocal<Boolean> login=ThreadLocal.withInitial(()->Boolean.FALSE);

    @SuppressWarnings("rawtypes")
    private final static ThreadLocal<TokenService> tokenService=new ThreadLocal<>();

    @SuppressWarnings("rawtypes")
    public static void setTokenService(TokenService tokenService){
        ThreadStorage.tokenService.set(tokenService);
    }

    @SuppressWarnings("rawtypes")
    public static TokenService getTokenService(){
        return tokenService.get();
    }

    public  static boolean isLogin(){
        return ThreadStorage.login.get();
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
        ThreadStorage.user.set(user);
    }

    public static void setUserType(String userType){
        ThreadStorage.userType.set(userType);
    }

    public static void setUserId(Object userId){
        ThreadStorage.userId.set(userId);
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
    }

}
