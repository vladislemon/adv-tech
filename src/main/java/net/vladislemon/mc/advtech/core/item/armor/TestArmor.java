package net.vladislemon.mc.advtech.core.item.armor;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.vladislemon.mc.advtech.core.energy.EnergyManager;
import net.vladislemon.mc.advtech.AdvancedTechnology;
import net.vladislemon.mc.advtech.core.Materials;
import net.vladislemon.mc.advtech.handler.InputHandler;
import net.vladislemon.mc.advtech.util.PlayerHelper;

/**
 * Created by slimon on 19-05-17.
 */
public class TestArmor extends EnergyArmor {

    public TestArmor(int armorType) {
        super(Materials.testArmor, armorType);
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
        return false;
    }

    @Override
    public int getDefaultTier() {
        return 1;
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        double ratio = source.isUnblockable() ? 0 : Math.min(EnergyManager.getEnergyEU(armor) / getEnergyForDamage(damage), 0.5);
        return new ArmorProperties(0, ratio, Integer.MAX_VALUE);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return Materials.testArmorDamageReduction[slot];
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        EnergyManager.useItemEU(stack, Math.min(getEnergyForDamage(damage), EnergyManager.getEnergyEU(stack)), entity);
    }

    public double getEnergyPerDamage() {
        return 50;
    }

    public double getEnergyForDamage(double damage) {
        return damage * getEnergyPerDamage();
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        super.onArmorTick(world, player, stack);
        if(AdvancedTechnology.inputHandler.getActionState(player, InputHandler.Action.JUMP_KEY)) {
            player.moveFlying(0, 4, 0.08F);
            player.motionY+=0.04F;
            PlayerHelper.resetPlayerInAirTime(player);
        }
    }
}
