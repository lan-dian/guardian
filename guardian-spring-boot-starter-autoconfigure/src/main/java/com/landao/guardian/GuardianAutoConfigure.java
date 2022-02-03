package com.landao.guardian;

import com.landao.guardian.core.TokenService;
import com.landao.guardian.interceptor.GuardianInterceptor;
import com.landao.guardian.config.GuardianProperties;
import com.landao.guardian.core.context.GuardianContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnBean(TokenService.class)
@ConditionalOnWebApplication
@EnableConfigurationProperties(GuardianProperties.class)
@Import({GuardianContext.class})
public class GuardianAutoConfigure {

    @Bean
    @ConditionalOnMissingBean(GuardianInterceptor.class)
    public GuardianInterceptor guardianInterceptor(){
        return new GuardianInterceptor();
    }

/*    @Bean
    @ConditionalOnMissingBean(CorsConfig.class)
    public CorsConfig corsConfig(){
        return new CorsConfig();
    }*/

}
