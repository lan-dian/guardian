package com.landao.guardian.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.landao.guardian.consts.TokenConst;
import com.landao.guardian.exception.token.TokenException;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 工具类不适合用户使用
 */
public abstract class TokenUtil {

    /**
     * 解析token
     * @param token token的有效部分
     * @param privateKey token密钥
     */
    public static DecodedJWT getDecodedJwt(String token,String privateKey){
        DecodedJWT decodedjwt=null;
        try {
            decodedjwt= JWT.require(Algorithm.HMAC256(privateKey))
                    .build().verify(token);
        }catch (Exception e){
            throw new TokenException("token非法:"+token);
        }
        return decodedjwt;
    }

    public static String getUserType(String token,String privateKey){
        DecodedJWT decodedJwt = getDecodedJwt(token, privateKey);
        return getUserType(decodedJwt);
    }


    public static String getUserType(DecodedJWT decodedJwt){
        return decodedJwt.getClaim(TokenConst.userType).asString();
    }

    /**
     * 获取token的有效部分,并且验证
     * @param token 原始header
     * @return token的有效部分
     */
    public static String getValidToken(String token,String prefix) {
        if(StringUtils.hasText(prefix)){//有前缀
            int prefixLength = prefix.length();
            String[] split = token.split(" ");
            if(split.length!=2) {
                throw new TokenException("token格式非法,应为:"+prefix+" "+"token的有效部分");
            }
            if(!Objects.equals(prefix,split[0])){
                throw new TokenException("token必须以"+prefix+"开头");
            }
            token=token.substring(prefixLength+1);
        }
        return token.trim();
    }

}
