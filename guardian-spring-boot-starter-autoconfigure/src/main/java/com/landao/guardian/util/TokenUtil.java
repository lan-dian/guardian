package com.landao.guardian.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.landao.guardian.GuardianProperties;
import com.landao.guardian.TokenService;
import com.landao.guardian.consts.TokenConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


public class TokenUtil {

    private static GuardianProperties guardianProperties;

    @Autowired
    private void setGuardianProperties(GuardianProperties guardianProperties) {
        TokenUtil.guardianProperties = guardianProperties;
    }

    public static String getToken() {
        HttpServletRequest request = NewxWebUtil.getRequest();
        return request.getHeader(getHeaderName());
    }

    public static String getVerifierToken(){
        return verifyToken(getToken());
    }

    public static String getUserType(){
        DecodedJWT decodedJwt = getDecodedJwt(verifyToken(getToken()));
        return decodedJwt.getClaim(TokenConst.userTypePrefix).asString();
    }

    public static DecodedJWT getDecodedJwt(String authorization){
        DecodedJWT decodedjwt=null;
        try {
            decodedjwt= JWT.require(Algorithm.HMAC256(guardianProperties.getToken().getPrivateKey()))
                    .build().verify(authorization);
        }catch (Exception e){
            throw new TokenService.TokenException("token非法:"+authorization);
        }
        return decodedjwt;
    }

    /**
     * 验证token并且提取出token的有效部分
     * @param token 从header里面获取的token
     * @return token的有效部分
     */
    public static String verifyToken(String token) {
        String prefix = guardianProperties.getToken().getPrefix();
        if(StringUtils.hasText(prefix)){
            prefix=prefix.trim();
            int length = prefix.length();
            String[] split = token.split(" ");
            if(split.length!=2) {
                throw new TokenService.TokenException("token格式不合法");
            }
            if(!Objects.equals(prefix,split[0])){
                throw new TokenService.TokenException("token必须以"+prefix+"开头");
            }
            return token.substring(length+1);
        }else {
            return token;
        }
    }

    /**
     * 请求头中是否传递了token
     */
    public static boolean hasToken() {
        HttpServletRequest request = NewxWebUtil.getRequest();
        String header = request.getHeader(getHeaderName());
        return StringUtils.hasText(header);
    }

    private static String getHeaderName() {
        return guardianProperties.getToken().getHeaderName();
    }

}
