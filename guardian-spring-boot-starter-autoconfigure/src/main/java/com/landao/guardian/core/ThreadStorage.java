package com.landao.guardian.core;


import com.landao.guardian.exception.author.UnLoginException;

public class ThreadStorage<T,R> {

    private final ThreadLocal<T> user = new ThreadLocal<>();

    private final ThreadLocal<String> userType=new ThreadLocal<>();

    private final ThreadLocal<R> userId=new ThreadLocal<>();

    private final ThreadLocal<Boolean> login=new ThreadLocal<>();

    @SuppressWarnings("rawtypes")
    private final ThreadLocal<TokenService> tokenService=new ThreadLocal<>();

    @SuppressWarnings("rawtypes")
    public void setTokenService(TokenService tokenService){
        this.tokenService.set(tokenService);
    }

    @SuppressWarnings("rawtypes")
    public TokenService getTokenService(){
        return this.tokenService.get();
    }

    public boolean isLogin(){
        Boolean login = this.login.get();
        if(login==null){
            this.login.set(Boolean.FALSE);
        }
        return this.login.get();
    }

    public void login(){
        login.set(Boolean.TRUE);
    }

    public void outLogin(){
        login.set(Boolean.FALSE);
    }

    public T getUser() {
        checkLogin();
        return user.get();
    }

    public String getUserType() {
        checkLogin();
        return userType.get();
    }

    public R getUserId() {
        checkLogin();
        return userId.get();
    }

    public void setUser(T user){
        this.user.set(user);
    }

    public void setUserType(String userType){
        this.userType.set(userType);
    }

    public void setUserId(R userId){
        this.userId.set(userId);
    }

    private void checkLogin(){
        if(!isLogin()){
            throw new UnLoginException();
        }
    }

}
