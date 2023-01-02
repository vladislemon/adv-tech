package net.vladislemon.mc.advtech.init;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by user on 1/7/2017.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Dependency {

    String type();

    String name();

    String modid() default "";

    /*
    * currently not used
     */
    String version() default "";
}
