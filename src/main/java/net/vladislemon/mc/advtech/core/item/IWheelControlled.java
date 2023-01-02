package net.vladislemon.mc.advtech.core.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by slimon on 16-05-17.
 */
public interface IWheelControlled {

    void onWheelUp(EntityPlayer player, ItemStack stack);

    void onWheelDown(EntityPlayer player, ItemStack stack);
}
