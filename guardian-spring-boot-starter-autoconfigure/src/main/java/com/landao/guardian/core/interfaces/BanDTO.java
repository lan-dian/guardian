package com.landao.guardian.core.interfaces;

public interface BanDTO {

    /**
     * 判断是否被ban
     */
    boolean isBan();

    /**
     * 如果用户被ban,返回给他的提示如下
     */
    String getThrowMsg();

}
