package com.landao.guardian.util;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 可以给用户使用的工具类
 */
public abstract class GuardianUtil {

    public static Set<String> toLowerCase(Set<String> set){
        return set.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }

}
