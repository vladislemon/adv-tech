package net.vladislemon.mc.advtech.init;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by slimon on 19-05-17.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ArmorItem {

    String nameOfSet();

    ArmorType type();

    enum ArmorType {
        HELMET,
        CHESTPLATE,
        LEGGINS,
        BOOTS
    }
}
