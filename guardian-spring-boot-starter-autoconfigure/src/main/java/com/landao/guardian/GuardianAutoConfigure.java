package com.landao.guardian;


import com.landao.guardian.interceptor.GuardianInterceptor;
import com.landao.guardian.config.GuardianProperties;
import com.landao.guardian.core.GuardianContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.Resource;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(GuardianProperties.class)
@Import({GuardianContext.class})
public class GuardianAutoConfigure {

    @Resource
    private GuardianProperties guardianProperties;

    @Bean
    @ConditionalOnMissingBean(GuardianInterceptor.class)
    public GuardianInterceptor guardianInterceptor(){
        return new GuardianInterceptor();
    }


    @Bean
    @ConditionalOnMissingBean(CorsFilter.class)
    public CorsFilter corsFilter() {
        GuardianProperties.Cors cors = guardianProperties.getCors();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(cors.getMapping(), buildConfig(cors));
        return new CorsFilter(source);
    }

    private CorsConfiguration buildConfig(GuardianProperties.Cors cors) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //*表示对所有的地址都可以访问
        corsConfiguration.addAllowedOrigin("*");
        //跨域的请求头
        corsConfiguration.addAllowedHeader("*");
        //跨域的请求方法
        corsConfiguration.addAllowedMethod("*");
        //可以携带cookie
        corsConfiguration.setAllowCredentials(cors.getAllowCookie());
        //配置客户端可以缓存来自飞行前请求的响应多长时间（以秒为单位）
        corsConfiguration.setMaxAge(cors.getMaxAge());
        return corsConfiguration;
    }

}
