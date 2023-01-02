package net.vladislemon.mc.advtech.core.item;

import cofh.api.energy.IEnergyContainerItem;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.vladislemon.mc.advtech.Constants;
import net.vladislemon.mc.advtech.core.energy.EnergyManager;
import net.vladislemon.mc.advtech.core.energy.IEnergyItem;
import net.vladislemon.mc.advtech.util.BaseHelper;

/**
 * Created by slimon on 22-10-17.
 */
public class UniversalCharger extends Electromagnet {

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int tick, boolean b) {
        //super.onUpdate(stack, world, entity, tick, b);
        if (isEnabled(stack)) {
            if (entity instanceof EntityPlayer) {
                InventoryPlayer inventory = ((EntityPlayer) entity).inventory;
                for (ItemStack itemStack : inventory.armorInventory) {
                    EnergyManager.extractEnergyEU(stack, tryChargeItem(itemStack, EnergyManager.getEnergyEU(stack),
                            getTier(stack)), true, false);
                }
                for (ItemStack itemStack : inventory.mainInventory) {
                    EnergyManager.extractEnergyEU(stack, tryChargeItem(itemStack, EnergyManager.getEnergyEU(stack),
                            getTier(stack)), true, false);
                }
            }
        }
    }

    private double tryChargeItem(ItemStack itemStack, double maxCharge, int chargerTier) {
        if (itemStack != null) {
            Item item = itemStack.getItem();
            if (item != null) {
                if (item instanceof IEnergyItem) {
                    return EnergyManager.receiveEnergyEU(itemStack, maxCharge, false, false);
                }
                if (item instanceof IElectricItem) {
                    return ElectricItem.manager.charge(itemStack, maxCharge, chargerTier, false, false);
                    //return EnergyManager.receiveEnergyEU(itemStack, maxCharge, true, false);
                }
                if (item instanceof IEnergyContainerItem) {
                    return ((IEnergyContainerItem) item).receiveEnergy(itemStack,
                            (int) Math.floor(maxCharge * Constants.EU_TO_RF_RATIO), BaseHelper.isClient()) / Constants.EU_TO_RF_RATIO;
                }
            }
        }
        return 0;
    }

    @Override
    public double getDefaultEnergy() {
        return 0;
    }

    @Override
    public double getDefaultMaxEnergy() {
        return 40000000;
    }

    @Override
    public double getDefaultMaxTransfer() {
        return 80000;
    }

    @Override
    public boolean isDefaultProvideEU() {
        return true;
    }

    @Override
    public int getDefaultTier() {
        return 4;
    }
}
