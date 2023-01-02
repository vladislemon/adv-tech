package net.vladislemon.mc.advtech.core.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.vladislemon.mc.advtech.core.item.tool.ImprovableTool;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Notezway on 15.12.2015.
 */
public class ImprovableToolRecipes implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        ItemStack[] items = getToolAndComponents(inventory);
        return items != null &&
                ((ImprovableTool)items[0].getItem()).canCraft(items[0], Arrays.copyOfRange(items, 1, items.length));
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack[] items = getToolAndComponents(inventory);
        return items == null ? null :
                ((ImprovableTool)items[0].getItem()).onCrafting(items[0], Arrays.copyOfRange(items, 1, items.length));
    }

    //first element in returned array is tool, others are components
    public ItemStack[] getToolAndComponents(InventoryCrafting inventory) {
        ArrayList<ItemStack> components = new ArrayList<ItemStack>();
        ItemStack item, tool = null;
        for(int i = 0; i < inventory.getSizeInventory(); i++) {
            item = inventory.getStackInSlot(i);
            if(item == null) continue;
            if(item.getItem() instanceof ImprovableTool) {
                tool = item;
                continue;
            }
            components.add(item);
        }
        if(tool == null) return null;
        ItemStack[] ret = new ItemStack[components.size() + 1];
        ItemStack[] arr = components.toArray(new ItemStack[components.size()]);
        ret[0] = tool;
        System.arraycopy(arr, 0, ret, 1, arr.length);
        return ret;
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}
