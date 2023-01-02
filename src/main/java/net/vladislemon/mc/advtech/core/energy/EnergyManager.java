package net.vladislemon.mc.advtech.core.energy;

import ic2.api.item.ElectricItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.vladislemon.mc.advtech.Constants;

/**
 * Created by user on 1/7/2017.
 */
public class EnergyManager {

    private static final String energy_nbt = "charge";
    private static final String max_energy_nbt = "max_charge";
    private static final String max_transfer_nbt = "max_transfer";
    private static final String can_provide_eu_nbt = "can_provide_eu";
    private static final String energy_tier_nbt = "energy_tier";

    public static void setDefaultValues(ItemStack stack, IEnergyItem item) {
        setMaxEnergyEU(stack, item.getDefaultMaxEnergy());
        setMaxTransferEU(stack, item.getDefaultMaxTransfer());
        setProvideEU(stack, item.isDefaultProvideEU());
        setTier(stack, item.getDefaultTier());
        setEnergyEU(stack, item.getDefaultEnergy());
    }

    public static void checkTag(ItemStack container) {
        if(!container.hasTagCompound()) {
            container.setTagCompound(new NBTTagCompound());
        }
    }

    public static void updateMeta(ItemStack container) {
        container.setItemDamage(Math.max(((int)Math.round((container.getMaxDamage() - 1) * (1 - getEnergyEU(container) / getMaxEnergyEU(container)))) + 1, 0));
    }

    public static double getEnergyEU(ItemStack container) {
        checkTag(container);
        return container.getTagCompound().getDouble(energy_nbt);
    }

    public static double getEnergyRF(ItemStack container) {
        return getEnergyEU(container) * Constants.EU_TO_RF_RATIO;
    }

    public static void setEnergyEU(ItemStack container, double energy) {
        checkTag(container);
        container.getTagCompound().setDouble(energy_nbt, Math.max(Math.min(energy, getMaxEnergyEU(container)), 0));
        updateMeta(container);
    }

    public static void setEnergyRF(ItemStack container, double energy) {
        setEnergyEU(container, energy / Constants.EU_TO_RF_RATIO);
    }

    public static double getMaxEnergyEU(ItemStack container) {
        checkTag(container);
        return container.getTagCompound().getDouble(max_energy_nbt);
    }

    public static double getMaxEnergyRF(ItemStack container) {
        return getMaxEnergyEU(container) * Constants.EU_TO_RF_RATIO;
    }

    public static void setMaxEnergyEU(ItemStack container, double energy) {
        checkTag(container);
        container.getTagCompound().setDouble(max_energy_nbt, Math.max(energy, 0));
    }

    public static void setMaxEnergyRF(ItemStack container, double energy) {
        setMaxEnergyEU(container, energy / Constants.EU_TO_RF_RATIO);
    }

    public static double getMaxTransferEU(ItemStack container) {
        checkTag(container);
        return container.getTagCompound().getDouble(max_transfer_nbt);
    }

    public static double getMaxTransferRF(ItemStack container) {
        return getMaxTransferEU(container) * Constants.EU_TO_RF_RATIO;
    }

    public static void setMaxTransferEU(ItemStack container, double energy) {
        checkTag(container);
        container.getTagCompound().setDouble(max_transfer_nbt, Math.max(energy, 0));
    }

    public static void setMaxTransferRF(ItemStack container, double energy) {
        setMaxTransferEU(container, energy / Constants.EU_TO_RF_RATIO);
    }

    public static boolean canProvideEU(ItemStack container) {
        checkTag(container);
        return container.getTagCompound().getBoolean(can_provide_eu_nbt);
    }

    public static void setProvideEU(ItemStack container, boolean provide) {
        checkTag(container);
        container.getTagCompound().setBoolean(can_provide_eu_nbt, provide);
    }

    public static int getTier(ItemStack container) {
        checkTag(container);
        return container.getTagCompound().getInteger(energy_tier_nbt);
    }

    public static void setTier(ItemStack container, int tier) {
        checkTag(container);
        container.getTagCompound().setInteger(energy_tier_nbt, tier);
    }

    public static double receiveEnergyEU(ItemStack container, double maxReceive, boolean ignoreLimit, boolean simulate) {
        checkTag(container);
        double energy = getEnergyEU(container);
        maxReceive = ignoreLimit ? maxReceive : Math.min(getMaxTransferEU(container), maxReceive);
        double energyReceived = Math.min(getMaxEnergyEU(container) - energy, maxReceive);

        if (!simulate) {
            setEnergyEU(container, energy + energyReceived);
        }
        return energyReceived;
    }

    public static double extractEnergyEU(ItemStack container, double maxExtract, boolean ignoreLimit, boolean simulate) {
        checkTag(container);
        double energy = getEnergyEU(container);
        maxExtract = ignoreLimit ? maxExtract : Math.min(getMaxTransferEU(container), maxExtract);
        double energyExtracted = Math.min(energy, maxExtract);

        if (!simulate) {
            setEnergyEU(container, energy - energyExtracted);
        }
        return energyExtracted;
    }

    public static int receiveEnergyRF(ItemStack container, int maxReceive, boolean ignoreLimit, boolean simulate) {
        checkTag(container);
        double energy = getEnergyRF(container);
        maxReceive = ignoreLimit ? maxReceive : (int)Math.floor(Math.min(getMaxTransferRF(container), maxReceive));
        int energyReceived = (int) Math.floor(Math.min(getMaxEnergyRF(container) - energy, maxReceive));

        if (!simulate) {
            setEnergyRF(container, energy + energyReceived);
        }
        return energyReceived;
    }

    public static int extractEnergyRF(ItemStack container, int maxExtract, boolean ignoreLimit, boolean simulate) {
        checkTag(container);
        double energy = getEnergyRF(container);
        maxExtract = ignoreLimit ? maxExtract : (int)Math.floor(Math.min(getMaxTransferRF(container), maxExtract));
        int energyExtracted = (int) Math.floor(Math.min(energy, maxExtract));

        if (!simulate) {
            setEnergyRF(container, energy - energyExtracted);
        }
        return energyExtracted;
    }

    public static int getRFEnergyStored(ItemStack container) {
        return (int) Math.floor(getEnergyRF(container));
    }

    public static int getMaxRFEnergyStored(ItemStack container) {
        return (int) Math.floor(getMaxEnergyRF(container));
    }

    public static boolean useItemEU(ItemStack container, double amountEU, EntityLivingBase entity) {
        //System.out.println(amountEU);
        boolean used = Constants.IC2_LOADED && ElectricItem.manager.use(container, amountEU, entity);
        return used || (extractEnergyEU(container, amountEU, true, true) >= amountEU && extractEnergyEU(container, amountEU, true, false) >= amountEU);
    }

    public static boolean canUseItemEU(ItemStack container, double amountEU) {
        return getEnergyEU(container) >= amountEU;
    }

    public static double getStoredEnergyRatio(ItemStack container) {
        return getEnergyEU(container) / getMaxEnergyEU(container);
    }

}
