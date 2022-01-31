package com.landao.guardian;

import com.landao.guardian.core.TokenAspect;
import com.landao.guardian.util.TokenUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(GuardianProperties.class)
@Import({CorsConfig.class, TokenAspect.class, TokenUtil.class})
public class GuardianAutoConfigure {




}
