package com.landao.guardian.annotations;

import com.landao.guardian.consts.GuardianConst;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GuardianService {

    String userType() default GuardianConst.defaultUserType;

}
