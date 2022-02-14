package com.landao.guardian;

import com.landao.guardian.annotations.system.Handler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@ComponentScan(basePackages = "com.landao.guardian.core",
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Handler.class))
public class HandlerScanner {

}
