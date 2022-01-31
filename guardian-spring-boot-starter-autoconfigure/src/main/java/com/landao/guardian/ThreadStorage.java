package com.landao.guardian;


import com.landao.guardian.exception.author.UnLoginException;

public class ThreadStorage<T,R> {

    private final ThreadLocal<T> user = new ThreadLocal<>();

    private final ThreadLocal<String> userType=new ThreadLocal<>();

    private final ThreadLocal<R> userId=new ThreadLocal<>();

    public T getUser() {
        T user = this.user.get();
        if(user==null){
            throw new UnLoginException();
        }
        return user;
    }

    public String getUserType() {
        String userType = this.userType.get();
        if(userType==null){
            throw new UnLoginException();
        }
        return userType;
    }

    public R getUserId() {
        R userId = this.userId.get();
        if(userId==null){
            throw new UnLoginException();
        }
        return userId;
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

}
