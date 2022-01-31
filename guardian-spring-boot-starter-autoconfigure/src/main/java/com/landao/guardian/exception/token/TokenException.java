package com.landao.guardian.exception.token;

/**
 * 解析token过程中发生的异常
 */
public class TokenException extends RuntimeException {

    public TokenException(String message) {
        super(message);
    }

}