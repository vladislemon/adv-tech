package net.vladislemon.mc.advtech.util;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class WorldBlock {

    public int x, y, z;

    public WorldBlock(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Object o) {
        if(o != null && o instanceof WorldBlock) {
            WorldBlock wb = (WorldBlock) o;
            return wb.x == this.x && wb.y == this.y && wb.z == this.z;
        }
        return false;
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(x).append(y).append(z).toHashCode();
    }

    public String toString() {
        return "x = " + x + "; y = " + y + "; z = " + z + ";";
    }
}
