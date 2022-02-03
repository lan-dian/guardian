package com.landao.guardian.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

@ConfigurationProperties(prefix = "guardian")
public class GuardianProperties {

    private Token token=new Token();

    private Cors cors=new Cors();

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    private Interceptor interceptor=new Interceptor();

    public GuardianProperties() {
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Cors getCors() {
        return cors;
    }

    public void setCors(Cors cors) {
        this.cors = cors;
    }

    public static class Token{

        private String privateKey;

        private String prefix="Bearer";

        private String headerName="Authorization";

        public Token() {
        }

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            if(!StringUtils.hasText(headerName)){
                throw new IllegalArgumentException("headerName不能为空字符串");
            }
            this.headerName = headerName;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }

    public static class Cors {

        private String mapping ="/**";

        public Cors() {
        }

        public String getMapping() {
            return mapping;
        }

        public void setMapping(String mapping) {
            this.mapping = mapping;
        }

    }

    public static class Interceptor{

        private Integer order=0;

        private String[] excludePatterns;

        public Interceptor() {
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public String[] getExcludePatterns() {
            return excludePatterns;
        }

        public void setExcludePatterns(String[] excludePatterns) {
            this.excludePatterns = excludePatterns;
        }
    }

}
