package net.vladislemon.mc.advtech.core.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.common.ForgeHooks;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ===
 * Created by slimon on 02.11.2015.
 */
public abstract class BaseTool extends ItemTool {

    protected Set<Block> mineableBlocks;

    public BaseTool(ToolMaterial material, Set<Block> mineableBlocks, float vsEntityDamage) {
        super(vsEntityDamage, material, mineableBlocks);
        this.mineableBlocks = mineableBlocks;
    }

    public float getDigSpeed(ItemStack tool, Block block, int meta) {
        if(isEffective(tool, block, meta))
            return efficiencyOnProperMaterial;
        return 1.0F;
    }

    public float func_150893_a(ItemStack stack, Block block) {
        return this.canHarvestBlock(block, stack)?this.efficiencyOnProperMaterial:super.func_150893_a(stack, block);
    }

    public boolean isEffective(ItemStack tool, Block block, int meta) {
        return this.canHarvestBlock(block, tool) || (mineableBlocks != null && mineableBlocks.contains(block))
                || ForgeHooks.isToolEffective(tool, block, meta);
    }

    public boolean isSilkTouchEnabled(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack) != 0;
    }

    public boolean isFortuneEnabled(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack) != 0;
    }

    public int getFortuneLevel(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);
    }

    public void setSilkTouch(ItemStack stack, int level) {
        setEnchantment(stack, Enchantment.silkTouch.effectId, level);
    }

    public void setFortune(ItemStack stack, int level) {
        setEnchantment(stack, Enchantment.fortune.effectId, level);
    }

    public void setEnchantment(ItemStack stack, int id, int level) {
        Map enchantments = new LinkedHashMap();
        enchantments.put(id, level);
        EnchantmentHelper.setEnchantments(enchantments, stack);
    }

    public void addEnchantment(ItemStack stack, int id, int level) {
        Map enchantments = EnchantmentHelper.getEnchantments(stack);
        enchantments.put(id, level);
        EnchantmentHelper.setEnchantments(enchantments, stack);
    }

    public void removeEnchantments(ItemStack stack) {
        EnchantmentHelper.setEnchantments(new LinkedHashMap<Integer, Integer>(), stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        //info.addAll(getHudInfo(itemStack));
    }
}
