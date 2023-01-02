package net.vladislemon.mc.advtech.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.vladislemon.mc.advtech.core.energy.EnergyManager;

import java.util.List;

/**
 * Created by user on 1/7/2017.
 */
public class TestEnergyItem extends EnergyItem {

    public TestEnergyItem() {
    }

    @Override
    public double getDefaultEnergy() {
        return 0;
    }

    @Override
    public double getDefaultMaxEnergy() {
        return 1000;
    }

    @Override
    public double getDefaultMaxTransfer() {
        return 10;
    }

    @Override
    public boolean isDefaultProvideEU() {
        return true;
    }

    @Override
    public int getDefaultTier() {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        ItemStack stack = new ItemStack(item);
        EnergyManager.setDefaultValues(stack, this);
        itemList.add(stack);
    }
}
