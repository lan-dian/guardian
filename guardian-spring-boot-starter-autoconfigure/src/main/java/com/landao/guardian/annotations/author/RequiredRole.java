package com.landao.guardian.annotations.author;


import com.landao.guardian.consts.TokenConst;
import com.landao.guardian.enums.LogicType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface RequiredRole {

    String[] requiredRole() default {};

    String userType() default TokenConst.userType;

    LogicType logicType() default LogicType.Or;

}
