package com.landao.guardian.exception.author;


import org.springframework.http.HttpStatus;

/**
 * 用户未登录异常
 * @apiNote 推荐状态码 {@link org.springframework.http.HttpStatus}
 * HttpStatus.UNAUTHORIZED 401
 */
public class UnLoginException extends RuntimeException{

    public UnLoginException() {
        throw new UnLoginException("用户未登陆");
    }

    public UnLoginException(String message) {
        super(message);
    }

}
