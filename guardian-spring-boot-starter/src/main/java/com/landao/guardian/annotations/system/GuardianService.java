package com.landao.guardian.annotations.system;

import com.landao.guardian.consts.GuardianConst;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;


@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GuardianService {

    @AliasFor(
            annotation = Component.class
    )
    String value() default GuardianConst.defaultUserType;


}
