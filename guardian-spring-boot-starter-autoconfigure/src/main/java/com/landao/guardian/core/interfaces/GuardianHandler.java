package com.landao.guardian.core.interfaces;

import org.springframework.core.Ordered;


import java.lang.reflect.Method;
import java.util.Map;

public interface GuardianHandler extends Ordered {

    /**
     * 返回值决定是否继续往下拦截
     * @param method 你要执行的controller中的方法
     */
    boolean handler(Method method);

}
