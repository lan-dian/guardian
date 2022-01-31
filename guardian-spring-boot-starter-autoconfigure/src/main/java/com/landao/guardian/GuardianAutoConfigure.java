package com.landao.guardian;

import com.landao.guardian.aspect.AspectAutoConfigure;
import com.landao.guardian.util.TokenUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(GuardianProperties.class)
@Import({CorsConfig.class,TokenService.class, AspectAutoConfigure.class, TokenUtil.class})
public class GuardianAutoConfigure {




}
