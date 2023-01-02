package net.vladislemon.mc.advtech.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.vladislemon.mc.advtech.core.energy.EnergyManager;
import net.vladislemon.mc.advtech.util.BaseHelper;
import net.vladislemon.mc.advtech.util.NBTUtil;
import net.vladislemon.mc.advtech.util.WorldUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Notezway on 03.01.2016.
 */
public class Electromagnet extends EnergyItem {

    private final String STATE = "state";

    public Electromagnet() {
        this.setMaxStackSize(1);
        //super(ToolMaterial.IRON, null);
    }

    public double energyUsage() {
        return 1;
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

    public void onUpdate(ItemStack stack, World world, Entity entity, int tick, boolean b) {
        super.onUpdate(stack, world, entity, tick, b);
        tick(stack, world, entity, true);
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        double charge = EnergyManager.getEnergyEU(stack);
        if (charge < energyUsage() && !isEnabled(stack))
            EnergyManager.useItemEU(stack, 0.0D, player);
        charge = EnergyManager.getEnergyEU(stack);
        String msg = "";
        if (charge < energyUsage()) {
            disable(stack);
            /*if(BaseHelper.isServer()) {
                BaseHelper.messagePlayer(player, PlayerHelper.hasPermissionEx(player, "bukkit.command.ban.player") ? "yes" : "no");
            }*/
            msg += EnumChatFormatting.DARK_RED + BaseHelper.getLocalizedString("noEnergy");
        } else {
            if (isEnabled(stack)) {
                disable(stack);
                msg += EnumChatFormatting.RED + BaseHelper.getLocalizedString("Disabled");
            } else {
                enable(stack);
                msg += EnumChatFormatting.DARK_GREEN + BaseHelper.getLocalizedString("Enabled");
            }
        }
        msg += EnumChatFormatting.RESET;
        BaseHelper.messagePlayer(player, msg);
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        tick(entityItem.getEntityItem(), entityItem.worldObj, entityItem, false);
        return super.onEntityItemUpdate(entityItem);
    }

    public void tick(ItemStack stack, World world, Entity entity, boolean inInventory) {
        if (isEnabled(stack)) {
            if (BaseHelper.isServer()) {
                double radius = 16, velocity = 0.5, distance, velX, velY, velZ;
                double distThreshold = 0.5;
                ArrayList<Entity> entityItems = WorldUtil.getItemAndXPEntitiesAroundEntity(world, entity, radius);
                int count = 0, max = 30;
                for (Entity item : entityItems) {
                    if (count > max)
                        break;
                    if ((item != entity) && (!(item instanceof EntityItem) || ((EntityItem) item).delayBeforeCanPickup == 0)) {
                        distance = entity.getDistanceToEntity(item);
                        if (distance > distThreshold) {
                            velX = (entity.posX - item.posX) / distance * velocity / Math.max(1, distance);
                            velY = (entity.posY - item.posY) / distance * velocity / Math.max(1, distance);
                            velZ = (entity.posZ - item.posZ) / distance * velocity / Math.max(1, distance);
                            item.addVelocity(velX, velY, velZ);
                        } else {
                            //item.setPosition(entity.posX, entity.posY, entity.posZ);
                            item.posX = entity.posX;
                            item.posY = entity.posY;
                            item.posZ = entity.posZ;
                            item.isAirBorne = true;
                            item.motionX = 0;
                            item.motionY = 0;
                            item.motionZ = 0;
                        }
                        if (inInventory && entity instanceof EntityLivingBase) {
                            EnergyManager.useItemEU(stack, energyUsage(), (EntityLivingBase) entity);
                        } else {
                            EnergyManager.extractEnergyEU(stack, energyUsage(), true, false);
                        }
                        count++;
                    }
                }
            }
            double charge = EnergyManager.getEnergyEU(stack);
            if (charge < energyUsage())
                disable(stack);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        String msg = "";
        if (isEnabled(stack))
            msg += EnumChatFormatting.DARK_GREEN + BaseHelper.getLocalizedString("Enabled");
        else
            msg += EnumChatFormatting.RED + BaseHelper.getLocalizedString("Disabled");
        msg += EnumChatFormatting.RESET;
        list.add(msg);
        super.addInformation(stack, player, list, b);
    }

    /*public List<String> getHudInfo(ItemStack stack) {
        List<String> info = new LinkedList<String>();
        //info = super.getHudInfo(stack);
        String msg = "";
        if(isEnabled(stack))
            msg += "§2" + BaseHelper.getLocalizedString("Enabled");
        else
            msg += "§c" + BaseHelper.getLocalizedString("Disabled");
        msg += "§r";
        info.add(msg.substring(1, msg.length()-3));
        return info;
    }*/

    @Override
    public double getDefaultEnergy() {
        return 0;
    }

    @Override
    public double getDefaultMaxEnergy() {
        return 100000;
    }

    @Override
    public double getDefaultMaxTransfer() {
        return 1000;
    }

    @Override
    public boolean isDefaultProvideEU() {
        return false;
    }

    @Override
    public int getDefaultTier() {
        return 2;
    }
}
