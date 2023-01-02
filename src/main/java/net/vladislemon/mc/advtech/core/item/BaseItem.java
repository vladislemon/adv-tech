package net.vladislemon.mc.advtech.core.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by slimon
 * on 07.06.2015.
 */
public abstract class BaseItem extends Item {

    public BaseItem() {
    }

    public ItemStack getNewItemStack() {
        return new ItemStack(this);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {

    }
}
