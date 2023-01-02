package net.vladislemon.mc.advtech.init;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.vladislemon.mc.advtech.Constants;
import net.vladislemon.mc.advtech.Tags;

import java.util.HashMap;

/**
 * Created by slimon
 * on 08.06.2015.
 */
public class Config {

    public static Config instance;

    Configuration cfg;
    private final HashMap<String, Boolean> thingsSwitchMap;

    public Config(Configuration cfg) {
        instance = this;
        this.cfg = cfg;
        thingsSwitchMap = new HashMap<String, Boolean>();
        cfg.load();
    }

    public void save() {
        cfg.save();
    }

    /**
     * @param defaultValue must be a int, double, boolean or string
     * @param path must contain all category names and property name
     * @return Property, contain needed value
     */
    public Property getProperty(String comment, Object defaultValue, String... path) {
        String fullCategoryName = "";
        for(int i = 0; i < path.length - 1; i++) {
            fullCategoryName += path[i] + Configuration.CATEGORY_SPLITTER;
        }
        if(defaultValue instanceof Integer) {
            return cfg.get(fullCategoryName, path[path.length-1], (Integer) defaultValue, comment);
        }
        else if(defaultValue instanceof Double) {
            return cfg.get(fullCategoryName, path[path.length-1], (Double) defaultValue, comment);
        }
        else if(defaultValue instanceof Boolean) {
            return cfg.get(fullCategoryName, path[path.length-1], (Boolean) defaultValue, comment);
        }
        else if(defaultValue instanceof String) {
            return cfg.get(fullCategoryName, path[path.length-1], (String) defaultValue, comment);
        }
        return null;
    }

    public Property getProperty(Object defaultValue, String... path) {
        return getProperty(null, defaultValue, path);
    }

    public Property getItemOrBlockProperty(String itemOrBlockName, String propertyName, Object defaultValue) {
        return getProperty(defaultValue, Constants.CONFIG_ITEMSBLOCKS, itemOrBlockName, propertyName);
    }

    public boolean isItemOrBlockEnabled(String name) {
        if(thingsSwitchMap.containsKey(name)) {
            return thingsSwitchMap.get(name);
        } else {
            boolean state = getProperty(true, Constants.CONFIG_ITEMSBLOCKS, name,
                    Constants.CONFIG_ENABLED).getBoolean();
            thingsSwitchMap.put(name, state);
            return state;
        }
    }

    public boolean isModEnabled() {
        return getProperty("Global mod switch", true, Constants.CONFIG_GENERAL,
                Tags.MODID, Constants.CONFIG_ENABLED).getBoolean();
    }

    public boolean useIC2Recipes() {
        return getProperty("If true and IC2 mod loaded, ic2-based recipes will be used", true, Constants.CONFIG_GENERAL,
                Tags.MODID, Constants.IC2_RECIPES).getBoolean();
    }
}
