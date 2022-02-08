package com.landao.guardian.util;


import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 利用spring mvc上下文的实现的工具类
 */
public abstract class NewxWebUtils {

    public static HttpServletRequest getRequest(){
        return getRequestAttributes().getRequest();
    }

    public static HttpServletResponse getResponse(){
        return getRequestAttributes().getResponse();
    }

    /**
     * 设置属性值
     * @apiNote 不做覆盖判断,如果使用安全框架,属性名不要是user
     * @param name 属性名
     * @param data 数据
     */
    public static void setAttribute(String name,Object data){
        getRequest().setAttribute(name,data);
    }

    /**
     * 获取属性值
     * @apiNote 可能返回空值
     * @param name 属性名
     * @return 属性值
     */
    public static Object getAttribute(String name){
        return getRequest().getAttribute(name);
    }

    /**
     * 是否包含属性
     * @param name 属性名
     * @return 包含返回ture否则返回false
     */
    public static boolean containsAttribute(String name){
        return getAttribute(name) != null;
    }

    /**
     * 移除属性
     * @param name 属性名
     */
    public static void removeAttribute(String name){
        getRequest().removeAttribute(name);
    }

    private static ServletRequestAttributes getRequestAttributes(){
        try {
            return  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        }catch (Exception e){
            throw new IllegalArgumentException("非web环境");
        }
    }


}
