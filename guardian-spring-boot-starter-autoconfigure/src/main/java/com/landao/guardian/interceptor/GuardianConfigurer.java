package com.landao.guardian.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class GuardianConfigurer implements WebMvcConfigurer {

    @Resource
    private GuardianInterceptor guardianInterceptor;

    /**
     * 拦截所有路径
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(guardianInterceptor).addPathPatterns("/**");
    }

}
