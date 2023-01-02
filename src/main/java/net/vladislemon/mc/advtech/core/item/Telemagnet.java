package net.vladislemon.mc.advtech.core.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.vladislemon.mc.advtech.core.energy.EnergyManager;
import net.vladislemon.mc.advtech.util.BaseHelper;
import net.vladislemon.mc.advtech.util.WorldUtil;

import java.util.ArrayList;

/**
 * Created by user on 12/25/2016.
 */
public class Telemagnet extends Electromagnet {

    public double energyUsage() {
        return 10;
    }

    public void tick(ItemStack stack, World world, Entity entity, boolean inInventory) {
        if (isEnabled(stack)) {
            if (BaseHelper.isServer()) {
                double radius = 16;
                double distThreshold = 0.5;
                double distance;
                ArrayList<Entity> entityItems = WorldUtil.getItemAndXPEntitiesAroundEntity(world, entity, radius);
                int count = 0, max = 30;
                for (Entity item : entityItems) {
                    if (count > max)
                        break;
                    if (item != entity) {
                        if (item instanceof EntityItem) {
                            EntityItem entityItem = (EntityItem) item;
                            int delay = entityItem.delayBeforeCanPickup;
                            boolean throwByPlayer = false;
                            EntityPlayer thrower = null;
                            if (inInventory && entity instanceof EntityPlayer) {
                                thrower = getItemThrower(entityItem);
                                throwByPlayer = thrower == entity;
                            }
                            if (delay > 20 && (throwByPlayer || thrower == null)) return;
                        }
                        distance = entity.getDistanceToEntity(item);
                        if (distance > distThreshold) {
                            //Random rand = world.rand;
                            double x = entity.posX;// - 0.1 + rand.nextDouble() / 5;
                            double y = entity.posY;// - 0.1 + rand.nextDouble() / 5;
                            double z = entity.posZ;// - 0.1 + rand.nextDouble() / 5;
                            item.setPosition(x, y, z);
                            item.isAirBorne = true;
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

    private EntityPlayer getItemThrower(EntityItem item) {
        World world = item.worldObj;
        String name = item.func_145800_j();
        //System.out.println(name);
        return name != null ? world.getPlayerEntityByName(name) : null;
    }

    @Override
    public double getDefaultMaxEnergy() {
        return 1000000;
    }

    @Override
    public double getDefaultMaxTransfer() {
        return 10000;
    }

    @Override
    public int getDefaultTier() {
        return 3;
    }
}
