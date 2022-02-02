package com.landao.guardian.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.landao.guardian.config.GuardianProperties;
import com.landao.guardian.consts.TokenConst;
import com.landao.guardian.exception.token.TokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;


import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


public class GuardianContext {

    private static GuardianProperties guardianProperties;

    @Autowired
    private void setGuardianProperties(GuardianProperties guardianProperties) {
        GuardianContext.guardianProperties = guardianProperties;
    }

    /**
     * 获取token
     * @apiNote 请先执行hasToken方法判断,不然这里会抛出异常
     * 会对token的格式进行简单的检查
     * @return token的有效部分
     */
    public static String getToken() {
        HttpServletRequest request = NewxWebUtil.getRequest();
        String token = request.getHeader(getHeaderName());
        if(!StringUtils.hasText(token)){
            throw new TokenException("未获取到以"+guardianProperties.getToken().getHeaderName()+
                    "为请求头的token,请先用hasToken方法判断是否有token");
        }
        return getValidToken(token);
    }


    /**
     * 验证token并且提取出token的有效部分
     * @param token 从header里面获取的token
     * @return token的有效部分
     */
    private static String getValidToken(String token) {
        String prefix = guardianProperties.getToken().getPrefix();
        if(StringUtils.hasText(prefix)){//有前缀
            int prefixLength = prefix.length();
            String[] split = token.split(" ");
            if(split.length!=2) {
                throw new TokenException("token格式非法,应为:"+guardianProperties.getToken().getPrefix()+" "+"token的有效部分");
            }
            if(!Objects.equals(prefix,split[0])){
                throw new TokenException("token必须以"+prefix+"开头");
            }
            token=token.substring(prefixLength+1);
        }
        return token.trim();
    }

    /**
     * 获取userType
     */
    public static String getUserType(String token){
        DecodedJWT decodedJwt = getDecodedJwt(token);
        return decodedJwt.getClaim(TokenConst.userType).asString();
    }

    private static String getUserType(){
        return getUserType(getToken());
    }


    public static DecodedJWT getDecodedJwt(String token){
        DecodedJWT decodedjwt=null;
        try {
            decodedjwt= JWT.require(Algorithm.HMAC256(guardianProperties.getToken().getPrivateKey()))
                    .build().verify(token);
        }catch (Exception e){
            throw new TokenException("token非法:"+token);
        }
        return decodedjwt;
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
