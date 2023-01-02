package net.vladislemon.mc.advtech.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Created by slimon
 * on 24.03.2015.
 */
public class RayTrace {

    public static MovingObjectPosition rayTraceBlockFromEntity(World world, Entity entity, double distance, double yOffset, boolean includeLiquids) {
        Vec3 pos = Vec3.createVectorHelper(entity.posX, entity.posY + yOffset, entity.posZ);
        Vec3 look = entity.getLookVec();
        Vec3 target = Vec3.createVectorHelper(
                pos.xCoord + look.xCoord / look.lengthVector() * distance,
                pos.yCoord + look.yCoord / look.lengthVector() * distance,
                pos.zCoord + look.zCoord / look.lengthVector() * distance);
        return world.rayTraceBlocks(pos, target, includeLiquids);
    }

    public static MovingObjectPosition rayTraceBlockFromPlayer(World world, EntityPlayer player, double distance, boolean includeLiquids) {
        if(BaseHelper.isServer()) {
            double yOffset;
            if (player.isSneaking()) yOffset = 1.54;
            else yOffset = 1.62;
            //yOffset = player.yOffset;
            return rayTraceBlockFromEntity(world, player, distance, yOffset, includeLiquids);
        }
        MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;
        if(mop != null) return mop;
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static MovingObjectPosition rayTraceFromPlayer(EntityPlayer player, double distance) {
        return player.rayTrace(distance, 1F);
    }
}
