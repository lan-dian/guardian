package com.landao.guardian.util;

import com.landao.guardian.consts.GuardianConst;
import com.landao.guardian.core.GuardianContext;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AliasFor;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 可以给用户使用的工具类
 */
public class GuardianUtils {

    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext){
        GuardianUtils.applicationContext= applicationContext;
    }

    public static <T extends Enum<?>> Set<String> enumToString(Set<T> enums){
        return enums.stream().map(Enum::name).collect(Collectors.toSet());
    }

    public static String getRedisKey(String userType,Object userId){
        return GuardianConst.redisPrefix +":"+applicationContext.getId()+ ":" + userType + ":" + userId;
    }

}
