package com.landao.guardian;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@ComponentScan(basePackages = "com.landao.guardian",
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Aspect.class))
public class AspectScanner {
}
