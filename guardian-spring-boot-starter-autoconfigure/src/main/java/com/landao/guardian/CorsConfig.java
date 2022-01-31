package com.landao.guardian;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

public class CorsConfig implements WebMvcConfigurer {

    @Resource
    private GuardianProperties guardianProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(guardianProperties.getCors().getMapping())
                .allowedOriginPatterns("*")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

}