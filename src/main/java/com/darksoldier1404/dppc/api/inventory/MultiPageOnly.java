package com.darksoldier1404.dppc.api.inventory;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface MultiPageOnly{
    String message() default "This method can be used only when DInventory is usePage true.";
}
