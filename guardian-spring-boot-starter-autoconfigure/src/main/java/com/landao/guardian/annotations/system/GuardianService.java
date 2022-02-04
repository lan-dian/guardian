package com.landao.guardian.annotations.system;

import com.landao.guardian.consts.GuardianConst;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Inherited
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GuardianService {

    @AliasFor(value = "value",annotation = Component.class)
    String userType() default GuardianConst.beanPrefix+GuardianConst.defaultUserType;



}
