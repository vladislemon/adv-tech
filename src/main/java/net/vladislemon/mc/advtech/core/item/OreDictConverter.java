package net.vladislemon.mc.advtech.core.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.vladislemon.mc.advtech.AdvancedTechnology;
import net.vladislemon.mc.advtech.handler.InputHandler;
import net.vladislemon.mc.advtech.util.BaseHelper;
import net.vladislemon.mc.advtech.util.InventoryHelper;
import net.vladislemon.mc.advtech.util.NBTUtil;

import java.util.List;

/**
 * Created by Notezway on 11.05.2016.
 */
public class OreDictConverter extends BaseItem {

    private final String STATE = "state";
    private final String[] containsForOreDict = new String[]{"ore", "ingot"};

    public OreDictConverter() {
        this.setMaxStackSize(1);
    }

    public boolean isEnabled(ItemStack stack) {
        return NBTUtil.readByte(stack, STATE) > 0;
    }

    public void enable(ItemStack stack) {
        NBTUtil.writeByte(stack, STATE, (byte) 1);
    }

    public void disable(ItemStack stack) {
        NBTUtil.writeByte(stack, STATE, (byte) 0);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int tick, boolean b) {
        if (isEnabled(stack)) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (player.inventory != null) {
                    InventoryHelper.convertWithOreDict(player.inventory, containsForOreDict);
                }
            }
        }
        super.onUpdate(stack, world, entity, tick, b);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (AdvancedTechnology.inputHandler.getActionState(player, InputHandler.Action.MODE_KEY)) {
            String msg = "";
            if (isEnabled(stack)) {
                disable(stack);
                msg += EnumChatFormatting.RED +
                        BaseHelper.getLocalizedString("AutoMode") + ": " +
                        BaseHelper.getLocalizedString("Disabled");
            } else {
                enable(stack);
                msg += EnumChatFormatting.DARK_GREEN +
                        BaseHelper.getLocalizedString("AutoMode") + ": " +
                        BaseHelper.getLocalizedString("Enabled");
            }
            msg += EnumChatFormatting.RESET;
            BaseHelper.messagePlayer(player, msg);
        } else if (!AdvancedTechnology.inputHandler.getActionState(player, InputHandler.Action.SNEAK_KEY)) {
            InventoryHelper.convertWithOreDict(player.inventory, containsForOreDict);
        }
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
                             float xOffset, float yOffset, float zOffset) {
        Block block = world.getBlock(x, y, z);
        int blockMeta = world.getBlockMetadata(x, y, z);
        if (block.hasTileEntity(blockMeta)) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof IInventory) {
                InventoryHelper.convertWithOreDict((IInventory) tileEntity, containsForOreDict);
                String msg = EnumChatFormatting.YELLOW +
                        BaseHelper.getLocalizedString("InventoryDetected") +
                        EnumChatFormatting.RESET;
                BaseHelper.messagePlayer(player, msg);
            }
        }
        return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean b) {
        String msg = "";
        if (isEnabled(stack)) {
            msg += EnumChatFormatting.DARK_GREEN +
                    BaseHelper.getLocalizedString("AutoMode") + ": " +
                    BaseHelper.getLocalizedString("Enabled");
        } else {
            msg += EnumChatFormatting.RED +
                    BaseHelper.getLocalizedString("AutoMode") + ": " +
                    BaseHelper.getLocalizedString("Disabled");
        }
        msg += EnumChatFormatting.RESET;
        info.add(msg);
        super.addInformation(stack, player, info, b);
    }
}
