package net.vladislemon.mc.advtech.core.item.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.vladislemon.mc.advtech.util.BaseHelper;
import net.vladislemon.mc.advtech.util.BlockHarvester;
import net.vladislemon.mc.advtech.util.WorldBlock;
import net.vladislemon.mc.advtech.util.WorldUtil;

import java.util.List;

/**
 * Created by Notezway on 27.05.2016.
 */
public class CoolMiningTool extends EnergyImprovableTool {

    public CoolMiningTool() {
        super(ToolMaterial.EMERALD, null, 0, ToolMode.BASE);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        BlockHarvester harvester = new BlockHarvester(world).setBlockLimit(16*16*256).setDrawEffects(false).setDropBlocks(true)
                .setDropXp(true).setIgnoreLiquids(false).setIgnoreTileEntity(true).setIgnoreCreative(true).setPlaySound(false);
        harvester.setPlayer(player);
        List<WorldBlock> blocks = WorldUtil.getBlocksInChunkWithOreDict(world, (int)player.posX, (int)player.posZ);
        BaseHelper.messagePlayer(player, "Blocks: " + blocks.size());
        harvester.harvestBlocks(blocks);
        if(BaseHelper.isServer()) {
            /*AdvancedTechnology.network.sendToAll(new RayMessage(player.posX, player.posY + player.eyeHeight,
                    player.posZ, player.posX, player.posY + 25, player.posZ, 0, 200));*/
            BaseHelper.messagePlayer(player, "Yeah!");
        }
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public double getDefaultEnergy() {
        return 0;
    }

    @Override
    public double getDefaultMaxEnergy() {
        return 100000000;
    }

    @Override
    public double getDefaultMaxTransfer() {
        return 100000;
    }

    @Override
    public boolean isDefaultProvideEU() {
        return false;
    }

    @Override
    public int getDefaultTier() {
        return 4;
    }
}
