package com.landao.guardian.annotations.author;


import com.landao.guardian.consts.GuardianConst;
import com.landao.guardian.enums.LogicType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Repeatable(RequiredRoles.class)
public @interface RequiredRole {

    @AliasFor("roles")
    String[] value() default {};

    @AliasFor("value")
    String[] roles() default {};

    String userType() default GuardianConst.defaultUserType;

    LogicType logicType() default LogicType.Or;

}
