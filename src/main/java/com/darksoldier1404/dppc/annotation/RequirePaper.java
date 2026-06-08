package com.darksoldier1404.dppc.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface RequirePaper {
    String message() default "This can be used only when using Paper bukkit.";
}
