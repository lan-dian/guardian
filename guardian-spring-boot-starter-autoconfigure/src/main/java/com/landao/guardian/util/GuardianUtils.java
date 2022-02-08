package com.landao.guardian.util;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 可以给用户使用的工具类
 */
public abstract class GuardianUtils {

    public static Set<String> enumToString(Set<Enum<?>> enums){
        return enums.stream().map(Enum::name).collect(Collectors.toSet());
    }

}
