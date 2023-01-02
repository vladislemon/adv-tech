package net.vladislemon.mc.advtech.core.item.tool;

/**
 * Created by Notezway on 15.12.2015.
 */
public enum ToolMode {

    BASE(false),
    LOW_POWER(false),
    MINING_3X3(false),
    MINING_ORES(false),
    PICKUP_ITEMS(true);

    public boolean alwaysActive;

    ToolMode(boolean alwaysActive) {
        this.alwaysActive = alwaysActive;
    }
}
