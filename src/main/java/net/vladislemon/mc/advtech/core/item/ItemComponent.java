package net.vladislemon.mc.advtech.core.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.vladislemon.mc.advtech.util.BaseHelper;

import java.util.List;

/**
 * Created by Notezway on 11.05.2016.
 */
public class ItemComponent extends BaseItem {

    @SuppressWarnings("unchecked")
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        info.add(BaseHelper.getLocalizedString("UsedForCrafting"));
    }
}
