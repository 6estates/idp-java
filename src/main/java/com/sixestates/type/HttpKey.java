package com.sixestates.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author kechen, 22/11/23.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HttpKey {
    String value() default "";

    /**
     * ignore field or not
     *
     * @return
     */
    boolean ignore() default false;
}
