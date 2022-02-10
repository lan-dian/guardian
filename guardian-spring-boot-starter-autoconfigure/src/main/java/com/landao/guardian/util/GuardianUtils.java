package com.landao.guardian.util;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 可以给用户使用的工具类
 */
public abstract class GuardianUtils {

    public static <T extends Enum<?>> Set<String> enumToString(Set<T> enums){
        return enums.stream().map(Enum::name).collect(Collectors.toSet());
    }

}
