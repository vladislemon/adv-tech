package net.vladislemon.mc.advtech.core.item.armor;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.IElectricItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.vladislemon.mc.advtech.core.energy.EnergyManager;
import net.vladislemon.mc.advtech.core.energy.IEnergyItem;
import net.vladislemon.mc.advtech.Constants;

import java.util.List;

/**
 * Created by slimon on 19-05-17.
 */
public abstract class EnergyArmor extends BaseArmor implements IElectricItem, IEnergyContainerItem, IEnergyItem, ISpecialArmor {

    public EnergyArmor(ArmorMaterial armorMaterial, int armorType) {
        super(armorMaterial, armorType);
        this.setMaxDamage(27);
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);
        EnergyManager.setDefaultValues(stack, this);
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return EnergyManager.canProvideEU(itemStack);
    }

    @Override
    public Item getChargedItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public double getMaxCharge(ItemStack itemStack) {
        return EnergyManager.getMaxEnergyEU(itemStack);
    }

    @Override
    public int getTier(ItemStack container) {
        return EnergyManager.getTier(container);
    }

    @Override
    public double getTransferLimit(ItemStack itemStack) {
        return EnergyManager.getMaxTransferEU(itemStack);
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        return EnergyManager.receiveEnergyRF(container, maxReceive, false, simulate);
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return EnergyManager.extractEnergyRF(container, maxExtract, false, simulate);
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return EnergyManager.getRFEnergyStored(container);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return EnergyManager.getMaxRFEnergyStored(container);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        if(!Constants.IC2_LOADED) {
            list.add(getEnergyStored(stack) + " / " + getMaxEnergyStored(stack) +  " RF");
        }
        super.addInformation(stack, player, list, b);
    }

    public ItemStack getItemStack(Item item, double charge) {
        ItemStack ret = super.getNewItemStack();
        EnergyManager.setDefaultValues(ret, (IEnergyItem) item);
        EnergyManager.setEnergyEU(ret, charge);
        return ret;
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        itemList.add(this.getItemStack(item, Double.MAX_VALUE));
        itemList.add(this.getItemStack(item, 0.0D));
    }
}
