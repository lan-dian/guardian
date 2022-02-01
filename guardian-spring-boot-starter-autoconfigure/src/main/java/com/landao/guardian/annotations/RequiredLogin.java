package com.landao.guardian.annotations;

import com.landao.guardian.consts.GuardianConst;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface RequiredLogin {

    String[] onlyFor() default {};

    String[] forbidden() default {};

}
