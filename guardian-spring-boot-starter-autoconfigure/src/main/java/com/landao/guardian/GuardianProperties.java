package com.landao.guardian;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "guardian")
public class GuardianProperties {

    private Token token=new Token();

    private Cors cors=new Cors();

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

}
