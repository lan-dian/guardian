package com.landao.guardian.annotations.author;


import com.landao.guardian.consts.GuardianConst;
import com.landao.guardian.enums.LogicType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Repeatable(RequiredRoles.class)
public @interface RequiredRole {

    String[] roles() default {};

    String userType() default GuardianConst.defaultUserType;

    LogicType logicType() default LogicType.Or;

}
