package com.landao.guardian.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

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

        /**
         * token私钥
         */
        private String privateKey;

        /**
         * Token风格前缀
         */
        private String prefix="Bearer";

        /**
         * header请求头
         */
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

        /**
         * 跨域处理的路径
         */
        private String mapping ="/**";

        /**
         * 是否容许携带cookie
         */
        private Boolean allowCookie=Boolean.TRUE;

        /**
         * 配置客户端可以缓存来自飞行前请求的响应多长时间（以秒为单位）
         */
        private Long maxAge=3600L;

        public Cors() {
        }

        public String getMapping() {
            return mapping;
        }

        public void setMapping(String mapping) {
            this.mapping = mapping;
        }

        public Boolean getAllowCookie() {
            return allowCookie;
        }

        public void setAllowCookie(Boolean allowCookie) {
            this.allowCookie = allowCookie;
        }

        public Long getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(Long maxAge) {
            this.maxAge = maxAge;
        }
    }

    public static class Interceptor{

        /**
         * 拦截器执行的顺序
         */
        private Integer order=0;

        /**
         * 拦截的路径
         */
        private String[] includedPathPatterns ={"/**"};

        /**
         * 不拦截的路径
         */
        private String[] excludePathPatterns={};

        public Interceptor() {
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public String[] getExcludePathPatterns() {
            return excludePathPatterns;
        }

        public void setExcludePathPatterns(String[] excludePathPatterns) {
            this.excludePathPatterns = excludePathPatterns;
        }

        public String[] getIncludedPathPatterns() {
            return includedPathPatterns;
        }

        public void setIncludedPathPatterns(String[] includedPathPatterns) {
            this.includedPathPatterns = includedPathPatterns;
        }
    }

}
