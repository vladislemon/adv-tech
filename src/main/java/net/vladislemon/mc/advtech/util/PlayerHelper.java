package net.vladislemon.mc.advtech.util;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;

import java.util.ArrayList;

/**
 * Created by slimon
 * on 29.03.2015.
 */
public class PlayerHelper {

    public static ArrayList<ItemStack> addToInventory(EntityPlayer player, ArrayList<ItemStack> stacks) {
        return InventoryHelper.addToInventory(player.inventory, stacks);
    }

    public static int getFirstSlotWithItemStack(EntityPlayer player, ItemStack itemStack) {
        return InventoryHelper.getFirstSlotWithItemStack(player.inventory, itemStack);
    }

    public static int getFirstNotFilledSlotWithItemStack(EntityPlayer player, ItemStack itemStack) {
        return InventoryHelper.getFirstNotFilledSlotWithItemStack(player.inventory, itemStack);
    }

    public static boolean inventoryIsFilled(EntityPlayer player) {
        return InventoryHelper.inventoryIsFilled(player.inventory);
    }

    public static void resetPlayerInAirTime(EntityPlayer player) {
        player.fallDistance = 0.0F;
        player.distanceWalkedModified = 0.0F;
        if(player instanceof EntityPlayerMP) {
            ObfuscationReflectionHelper.setPrivateValue(NetHandlerPlayServer.class, ((EntityPlayerMP)player).playerNetServerHandler, 0, "field_147365_f", "floatingTickCount");
        }
    }
}
