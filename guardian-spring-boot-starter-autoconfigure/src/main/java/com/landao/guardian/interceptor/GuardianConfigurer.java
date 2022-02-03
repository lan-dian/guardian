package com.landao.guardian.interceptor;

import com.landao.guardian.config.GuardianProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class GuardianConfigurer implements WebMvcConfigurer {

    @Resource
    private GuardianProperties guardianProperties;

    @Resource
    private GuardianInterceptor guardianInterceptor;

    /**
     * 拦截所有路径
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        GuardianProperties.Interceptor interceptor = guardianProperties.getInterceptor();
        registry.addInterceptor(guardianInterceptor).addPathPatterns(interceptor.getIncludedPathPatterns())
                .excludePathPatterns(interceptor.getExcludePathPatterns())
                .order(interceptor.getOrder());
    }

}
