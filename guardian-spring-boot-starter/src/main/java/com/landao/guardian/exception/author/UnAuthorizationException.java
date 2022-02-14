package com.landao.guardian.exception.author;

import org.springframework.http.HttpStatus;

/**
 * 用户角色认证异常
 *  @apiNote 推荐状态码 {@link org.springframework.http.HttpStatus}
 *  HttpStatus.FORBIDDEN 403
 */
public class UnAuthorizationException extends RuntimeException{

    public UnAuthorizationException(String message) {
        super(message);
    }

}
