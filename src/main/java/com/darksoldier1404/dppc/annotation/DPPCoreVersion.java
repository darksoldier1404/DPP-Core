package com.darksoldier1404.dppc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.CLASS)
public @interface DPPCoreVersion {
    String value() default "";
}
