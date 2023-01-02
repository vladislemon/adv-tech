package net.vladislemon.mc.advtech.core.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;

/**
 * Created by slimon
 * on 29.03.2015.
 */
public class ItemLitRedstone extends ItemBlock {

    public ItemLitRedstone() {
        super(Blocks.lit_redstone_ore);
        GameRegistry.registerItem(this, "lit_redstone_ore");
    }
}
