package net.vladislemon.mc.advtech.core.item.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by slimon on 19-05-17.
 */
public abstract class BaseArmor extends ItemArmor {

    private String armorTexture;

    public BaseArmor(ArmorMaterial armorMaterial, int armorType) {
        super(armorMaterial, 0, armorType);
    }

    public void setArmorTexture(String armorTexture) {
        this.armorTexture = armorTexture;
    }

    public ItemStack getNewItemStack() {
        return new ItemStack(this);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {}

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return armorTexture;
    }
}
