package ua.tunepoint.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventTarget {

    /**
     * Name of the object field to be used as target, object is used if not specified
     */
    String value() default "";
}
