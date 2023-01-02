package net.vladislemon.mc.advtech.core;

import net.minecraft.util.DamageSource;

/**
 * Created by Notezway on 10.06.2016.
 */
public class ATDamageSource extends DamageSource {

    public ATDamageSource(String name) {
        super(name);
    }

    public static ATDamageSource common;
    static {
        common = new ATDamageSource("AT_common");
    }
}
