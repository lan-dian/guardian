package com.landao.guardian.annotations.author;


import com.landao.guardian.consts.GuardianConst;
import com.landao.guardian.entity.enums.LogicType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface RequiredRole {

    String[] roles() default {};

    String userType() default GuardianConst.defaultUserType;

    LogicType logicType() default LogicType.Or;

}
