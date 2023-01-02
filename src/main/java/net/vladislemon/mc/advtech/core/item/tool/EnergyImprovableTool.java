package net.vladislemon.mc.advtech.core.item.tool;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.IElectricItem;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.vladislemon.mc.advtech.core.energy.EnergyManager;
import net.vladislemon.mc.advtech.core.energy.IEnergyItem;
import net.vladislemon.mc.advtech.Constants;

import java.util.List;
import java.util.Set;

/**
 * Created by Notezway on 15.12.2015.
 */
public abstract class EnergyImprovableTool extends ImprovableTool implements IElectricItem, IEnergyContainerItem, IEnergyItem {

    public EnergyImprovableTool(ToolMaterial material, Set<Block> mineableBlocks, float vsEntityDamage, ToolMode... defaultModes) {
        super(material, mineableBlocks, vsEntityDamage, defaultModes);
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    public void useEnergy(ItemStack item, EntityPlayer player, double amount) {
        if(!player.capabilities.isCreativeMode) {
            EnergyManager.useItemEU(item, amount, player);
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        EnergyManager.useItemEU(stack, 0, player);
        return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        EnergyManager.useItemEU(stack, 0, player);
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public boolean hitEntity(ItemStack p_77644_1_, EntityLivingBase p_77644_2_, EntityLivingBase p_77644_3_) {
        return true;
    }

    public ItemStack getItemStack(Item item, double charge) {
        ItemStack ret = getItemStack();
        EnergyManager.setDefaultValues(ret, (IEnergyItem) item);
        EnergyManager.setEnergyEU(ret, charge);
        return ret;
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        itemList.add(this.getItemStack(item, Double.MAX_VALUE));
        itemList.add(this.getItemStack(item, 0.0D));
    }

    /*@SideOnly(Side.CLIENT)
    public List<String> getHudInfo(ItemStack stack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add(BaseHelper.getLocalizedString("PowerTier") + ": " + EnergyManager.getTier(stack));
        return info;
    }*/

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        if(!Constants.IC2_LOADED) {
            list.add(getEnergyStored(stack) + " / " + getMaxEnergyStored(stack) +  " RF");
        }
        super.addInformation(stack, player, list, b);
    }

    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
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
}
