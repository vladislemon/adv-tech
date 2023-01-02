package net.vladislemon.mc.advtech.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Notezway on 04.01.2016.
 */
public class InventoryHelper {

    public static ArrayList<ItemStack> addToInventory(IInventory inventory, ArrayList<ItemStack> stacks) {
        ArrayList<ItemStack> notAdded = new ArrayList<ItemStack>();
        ItemStack stack, cont;
        int slot, delta;
        if(inventory != null && stacks != null) {
            for (int i = 0; i < stacks.size(); i++) {
                stack = stacks.get(i);
                if (stack == null || stack.getItem() == null || stack.stackSize == 0) continue;
                if ((slot = getFirstNotFilledSlotWithItemStack(inventory, stacks.get(i))) != -1) {
                    cont = inventory.getStackInSlot(slot);
                    delta = Math.min(getMaxStackSize(inventory, stack) - cont.stackSize, stack.stackSize);
                    stack.stackSize -= delta;
                    cont.stackSize += delta;
                    if (stack.stackSize > 0) {
                        i--;
                    }
                    continue;
                }
                if ((slot = getFirstEmptySlot(inventory)) != -1 && inventory.isItemValidForSlot(slot, stack)) {
                    inventory.setInventorySlotContents(slot, stack.copy());
                    stack.stackSize = 0;
                }
            }
            for (ItemStack s : stacks) {
                if (s != null && s.stackSize > 0)
                    notAdded.add(s);
            }
        }
        return notAdded;
    }

    public static int getFirstEmptySlot(IInventory inventory) {
        if(inventory != null) {
            ItemStack stack;
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                stack = inventory.getStackInSlot(i);
                if (stack == null || stack.stackSize <= 0)
                    return i;
            }
        }
        return -1;
    }

    /*
    * quantity are ignored
    */
    public static int getFirstSlotWithItemStack(IInventory inventory, ItemStack itemStack) {
        if(inventory != null && itemStack != null) {
            ItemStack cont;
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                cont = inventory.getStackInSlot(i);
                if (cont != null && BaseHelper.areStacksEquals(cont, itemStack, false, true))
                    return i;
            }
        }
        return -1;
    }

    public static int getFirstNotFilledSlotWithItemStack(IInventory inventory, ItemStack itemStack) {
        if(inventory != null && itemStack != null && itemStack.getItem() != null) {
            ItemStack cont;
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                cont = inventory.getStackInSlot(i);
                if (cont != null && BaseHelper.areStacksEquals(cont, itemStack, false, true) &&
                        cont.stackSize < getMaxStackSize(inventory, cont))
                    return i;
            }
        }
        return -1;
    }

    public static int getMaxStackSize(IInventory inventory, ItemStack stack) {
        return Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
    }

    public static boolean inventoryIsFilled(IInventory inventory) {
        if(inventory != null) {
            ItemStack stack;
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                stack = inventory.getStackInSlot(i);
                if (stack == null || stack.stackSize < getMaxStackSize(inventory, stack))
                    return false;
            }
        }
        return true;
    }

    public static ItemStack[] getContent(IInventory inventory) {
        ItemStack[] content = new ItemStack[inventory.getSizeInventory()];
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            content[i] = inventory.getStackInSlot(i);
        }
        return content;
    }

    public static void convertWithOreDict(IInventory inventory, String[] contains) {
        HashMap<String, ItemStack> oreMap = new HashMap<String, ItemStack>();
        //ItemStack[] content = new ItemStack[inventory.getSizeInventory()];
        for(int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack entry = inventory.getStackInSlot(i);
            if(entry == null) continue;
            int[] ids = OreDictionary.getOreIDs(entry);
            for(int id : ids) {
                if(entry == null) break;
                String name = OreDictionary.getOreName(id);
                for(String c : contains) {
                    if(name.contains(c)) {
                        ItemStack entryX1 = new ItemStack(entry.getItem(), 1, entry.getItemDamage());
                        if(!oreMap.containsKey(name)) {
                            oreMap.put(name, entryX1);
                        }
                        else {
                            ItemStack target = oreMap.get(name);
                            if(!BaseHelper.areStacksEquals(entryX1, target)) {
                                int size = entry.stackSize;
                                inventory.setInventorySlotContents(i, null);
                                entry = null;
                                ArrayList<ItemStack> toAdd = new ArrayList<ItemStack>(1);
                                toAdd.add(new ItemStack(target.getItem(), size, target.getItemDamage()));
                                addToInventory(inventory, toAdd);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
