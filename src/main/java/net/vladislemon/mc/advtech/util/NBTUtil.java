package net.vladislemon.mc.advtech.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by slimon
 * on 15.06.2015.
 */
public class NBTUtil {

    public static void check(ItemStack stack) {
        if(stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
    }

    public static void writeInt(ItemStack stack, String name, int data) {
        check(stack);
        stack.getTagCompound().setInteger(name, data);
    }

    public static void writeFloat(ItemStack stack, String name, float data) {
        check(stack);
        stack.getTagCompound().setFloat(name, data);
    }

    public static void writeShort(ItemStack stack, String name, short data) {
        check(stack);
        stack.getTagCompound().setShort(name, data);
    }

    public static void writeByte(ItemStack stack, String name, byte data) {
        check(stack);
        stack.getTagCompound().setByte(name, data);
    }

    public static void writeString(ItemStack stack, String name, String data) {
        check(stack);
        stack.getTagCompound().setString(name, data);
    }

    public static void writeIntArray(ItemStack stack, String name, int[] data) {
        check(stack);
        stack.getTagCompound().setIntArray(name, data);
    }

    public static void writeByteArray(ItemStack stack, String name, byte[] data) {
        check(stack);
        stack.getTagCompound().setByteArray(name, data);
    }

    public static int readInt(ItemStack stack, String name) {
        check(stack);
        return stack.getTagCompound().getInteger(name);
    }

    public static float readFloat(ItemStack stack, String name) {
        check(stack);
        return stack.getTagCompound().getFloat(name);
    }

    public static short readShort(ItemStack stack, String name) {
        check(stack);
        return stack.getTagCompound().getShort(name);
    }

    public static byte readByte(ItemStack stack, String name) {
        check(stack);
        return stack.getTagCompound().getByte(name);
    }

    public static String readString(ItemStack stack, String name) {
        check(stack);
        return stack.getTagCompound().getString(name);
    }

    public static int[] readIntArray(ItemStack stack, String name) {
        check(stack);
        return stack.getTagCompound().getIntArray(name);
    }

    public static byte[] readByteArray(ItemStack stack, String name) {
        check(stack);
        return stack.getTagCompound().getByteArray(name);
    }
}
