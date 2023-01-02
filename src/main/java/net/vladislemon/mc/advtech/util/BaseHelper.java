package net.vladislemon.mc.advtech.util;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;
import net.vladislemon.mc.advtech.Constants;
import net.vladislemon.mc.advtech.Tags;

import java.util.Collection;

/**
 * Created by slimon
 * on 07.06.2015.
 */
public class BaseHelper {

    public static boolean isClient() {
        return FMLCommonHandler.instance().getEffectiveSide().isClient();
    }

    public static boolean isServer() {
        return FMLCommonHandler.instance().getEffectiveSide().isServer();
    }

    public static String getLocalizedString(String name) {
        return StatCollector.translateToLocal(Tags.MODID + "." + name);
    }

    public static void messagePlayer(EntityPlayer player, String message) {
        if(!isClient()) {
            ChatComponentTranslation msg = new ChatComponentTranslation(message);
            player.addChatMessage(msg);
        }
    }

    public static void messagePlayerLocalized(EntityPlayer player, String message) {
        messagePlayer(player, getLocalizedString(message));
    }

    public static void printToConsole(Object message) {
        FMLCommonHandler.instance().getFMLLogger().info(message);
    }

    public static void debugToConsole(Object message) {
        FMLCommonHandler.instance().getFMLLogger().debug(message);
    }

    public static boolean areStacksEquals(ItemStack stack1, ItemStack stack2, boolean checkSize, boolean checkDamage) {
        if (stack1 == null) return stack2 == null;
        return stack2 != null && stack1.getItem().getUnlocalizedName().equals(stack2.getItem().getUnlocalizedName()) &&
                (!checkSize || stack1.stackSize == stack2.stackSize) &&
                (!checkDamage || stack1.getItemDamage() == stack2.getItemDamage() ||
                        stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE ||
                        stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE);
    }

    public static boolean areStacksEquals(ItemStack stack1, ItemStack stack2) {
        return areStacksEquals(stack1, stack2, true, true);
    }

    public static boolean stackContains(ItemStack stack, ItemStack... array) {
        for(ItemStack element : array) {
            if(areStacksEquals(stack, element)) return true;
        }
        return false;
    }

    public static boolean stackContains(ItemStack stack, Collection<ItemStack> collection) {
        return stackContains(stack, collection.toArray(new ItemStack[collection.size()]));
    }

    public static boolean hasDuplicates(boolean checkSize, boolean checkDamage, ItemStack... array) {
        for(int i = 0; i < array.length; i++) {
            for(int j = i + 1; j < array.length; j++) {
                if(areStacksEquals(array[i], array[j], checkSize, checkDamage)) return true;
            }
        }
        return false;
    }
}
