package net.vladislemon.mc.advtech.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Notezway on 27.05.2016.
 */
public class BlockHarvester {

    private World world;
    private EntityPlayer player;
    private boolean silkTouch;
    private int fortune;
    private int blockLimit;
    private boolean dropBlocks;
    private IInventory dropTarget;
    private boolean playSound;
    private boolean playXpSound;
    private boolean drawEffects;
    private boolean dropXp;
    private EntityPlayer xpTarget;
    private boolean ignorePlayerCreative;
    private boolean addStats;
    private boolean addExhaustion;
    private int delayBeforePickup;
    private boolean ignoreTileEntity;
    private boolean ignoreLiquids;
    private float hardnessLimit;
    private boolean ignoreCreative;
    private boolean ignoreUnbreakable;

    private int harvested, hardHarvested;
    private Block block;

    public BlockHarvester(World world) {
        this.world = world;
        blockLimit = 1024;
        silkTouch = false;
        fortune = 0;
        dropBlocks = true;
        dropXp = true;
        playSound = true;
        playXpSound = true;
        drawEffects = true;
        addStats = true;
        addExhaustion = true;
        delayBeforePickup = 10;
        ignoreTileEntity = false;
        ignoreLiquids = true;
        hardnessLimit = Float.MAX_VALUE;
        ignoreUnbreakable = true;
        ignoreCreative = false;
    }

    public BlockHarvester reset() {
        harvested = 0;
        hardHarvested = 0;
        return this;
    }

    public boolean harvestBlock(int x, int y, int z) {
        if(harvested >= blockLimit) return false;
        if(world == null) return false;
        block = world.getBlock(x, y, z);
        int blockMeta = world.getBlockMetadata(x, y, z);
        float hardness = block.getBlockHardness(world, x, y, z);
        if(block.equals(Blocks.air)) return false;
        if(hardness > hardnessLimit || (hardness < 0 && ignoreUnbreakable)) return false;
        if(block.getMaterial().isLiquid() && ignoreLiquids) return false;
        if(block.hasTileEntity(blockMeta) && ignoreTileEntity) return false;
        ArrayList<ItemStack> drops = null;
        if(dropBlocks) {
            drops = getBlockDrops(x, y, z);
        }
        ForgeEventFactory.fireBlockHarvesting(drops, world, block, x, y, z, blockMeta, fortune, 1F, silkTouch, player);
        if(BaseHelper.isServer() && dropBlocks && (player == null || (dropTarget != null && dropTarget != player.inventory) || !player.capabilities.isCreativeMode || ignoreCreative)) {
            if(dropTarget != null) {
                drops = InventoryHelper.addToInventory(dropTarget, drops);
            }
            createItemStacksInWorld(x, y, z, drops);
        }
        if(BaseHelper.isServer() && playSound) {
            playBlockBreakSound(x, y, z, block);
        }
        if(drawEffects && BaseHelper.isClient()) {
            Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(x, y, z, block, blockMeta);
        }
        if(BaseHelper.isServer() && !silkTouch && dropXp) {
            if(xpTarget != null) {
                int exp = block.getExpDrop(world, blockMeta, fortune);
                if(exp > 0) {
                    xpTarget.xpCooldown = 2;
                    if(playXpSound) {
                        playXpSoundAtTarget();
                    }
                    xpTarget.addExperience(exp);
                }
            } else
            block.dropXpOnBlockBreak(world, x, y, z, block.getExpDrop(world, blockMeta, fortune));
        }
        //System.out.println("remove " + x + " " + y + " " + z);
        if(BaseHelper.isServer()) {
            world.setBlockToAir(x, y, z);
            world.removeTileEntity(x, y, z);
        }
        harvested++;
        if(hardness > 0) hardHarvested++;
        return true;
    }

    public boolean harvestBlock(WorldBlock wb) {
        return harvestBlock(wb.x, wb.y, wb.z);
    }

    public void harvestBlocks(int begin, int end, WorldBlock... blocks) {
        WorldBlock wb;
        for(int i = begin; i <= end; i++) {
            if(harvested >= blockLimit) break;
            wb = blocks[i];
            harvestBlock(wb);
        }
    }

    public void harvestBlocks(int begin, int end, List<WorldBlock> blocks) {
        WorldBlock wb;
        for(int i = begin; i <= end; i++) {
            //System.out.println(i);
            if(harvested >= blockLimit) break;
            wb = blocks.get(i);
            harvestBlock(wb);
        }
    }

    public void harvestBlocks(WorldBlock... blocks) {
        harvestBlocks(0, blocks.length-1, blocks);
    }

    public void harvestBlocks(List<WorldBlock> blocks) {
        harvestBlocks(0, blocks.size()-1, blocks);
    }

    public boolean harvestBlockByPlayer(int x, int y, int z) {
        if(player == null) return false;
        if(!world.canMineBlock(player, x, y, z)) return false;
        if(harvestBlock(x, y, z)) {
            if(addStats) {
                player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);
            }
            if(addExhaustion) {
                player.addExhaustion(0.005F);
            }
            return true;
        }
        return false;
    }

    public boolean harvestBlockByPlayer(WorldBlock wb) {
        return wb != null && harvestBlockByPlayer(wb.x, wb.y, wb.z);
    }

    public void harvestBlocksByPlayer(int begin, int end, WorldBlock... blocks) {
        WorldBlock wb;
        for(int i = begin; i <= end; i++) {
            if(harvested >= blockLimit) break;
            wb = blocks[i];
            harvestBlockByPlayer(wb);
        }
    }

    public void harvestBlocksByPlayer(int begin, int end, List<WorldBlock> blocks) {
        WorldBlock wb;
        for(int i = begin; i <= end; i++) {
            if(harvested >= blockLimit) break;
            wb = blocks.get(i);
            harvestBlockByPlayer(wb);
        }
    }

    public void harvestBlocksByPlayer(WorldBlock... blocks) {
        harvestBlocksByPlayer(0, blocks.length-1, blocks);
    }

    public void harvestBlocksByPlayer(List<WorldBlock> blocks) {
        harvestBlocksByPlayer(0, blocks.size()-1, blocks);
    }

    public ArrayList<ItemStack> getBlockDrops(int x, int y, int z) {
        ArrayList<ItemStack> drops;
        block = world.getBlock(x, y, z);
        int blockMeta = world.getBlockMetadata(x, y, z);
        if (silkTouch && block.canSilkHarvest(world, player, x, y, z, blockMeta)) {
            drops = new ArrayList<ItemStack>(1);
            if(block.equals(Blocks.lit_redstone_ore)) {
                drops.add(new ItemStack(Blocks.redstone_ore, 1, blockMeta));
            } else {
                drops.add(new ItemStack(block, 1, blockMeta));
            }
        } else {
            drops = block.getDrops(world, x, y, z, blockMeta, fortune);
        }
        return drops;
    }

    public void createItemStackInWorld(int x, int y, int z, ItemStack stack) {
        if(BaseHelper.isServer()) {
            if (stack != null) {
                EntityItem entity = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack);
                entity.delayBeforeCanPickup = delayBeforePickup;
                world.spawnEntityInWorld(entity);
            }
        }
    }

    public void createItemStacksInWorld(int x, int y, int z, ArrayList<ItemStack> stacks) {
        if(BaseHelper.isServer()) {
            EntityItem entity;
            for (ItemStack stack : stacks) {
                if (stack != null) {
                    entity = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack);
                    entity.delayBeforeCanPickup = delayBeforePickup;
                    world.spawnEntityInWorld(entity);
                }
            }
        }
    }

    public void playBlockBreakSound(int x, int y, int z, Block block) {
        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, block.stepSound.getBreakSound(),
                (block.stepSound.getVolume() + 1F)/2F, block.stepSound.getPitch()*0.8F);
    }

    public void playXpSoundAtTarget() {
        world.playSoundAtEntity(xpTarget, "random.orb", 0.1F, 0.5F * ((float) (Math.random() - Math.random()) * 0.7F + 1.8F));
    }

    public World getWorld() {
        return world;
    }

    public BlockHarvester setWorld(World world) {
        this.world = world;
        return this;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public BlockHarvester setPlayer(EntityPlayer player) {
        this.player = player;
        return this;
    }

    public boolean isSilkTouch() {
        return silkTouch;
    }

    public BlockHarvester setSilkTouch(boolean silkTouch) {
        this.silkTouch = silkTouch;
        return this;
    }

    public int getFortune() {
        return fortune;
    }

    public BlockHarvester setFortune(int fortune) {
        this.fortune = fortune;
        return this;
    }

    public int getBlockLimit() {
        return blockLimit;
    }

    public BlockHarvester setBlockLimit(int blockLimit) {
        this.blockLimit = blockLimit;
        return this;
    }

    public boolean isDropBlocks() {
        return dropBlocks;
    }

    public BlockHarvester setDropBlocks(boolean dropBlocks) {
        this.dropBlocks = dropBlocks;
        return this;
    }

    public IInventory getDropTarget() {
        return dropTarget;
    }

    public BlockHarvester setDropTarget(IInventory dropTarget) {
        this.dropTarget = dropTarget;
        return this;
    }

    public boolean isPlaySound() {
        return playSound;
    }

    public BlockHarvester setPlaySound(boolean playSound) {
        this.playSound = playSound;
        return this;
    }

    public boolean isPlayXpSound() {
        return playXpSound;
    }

    public BlockHarvester setPlayXpSound(boolean playXpSound) {
        this.playXpSound = playXpSound;
        return this;
    }

    public boolean isDrawEffects() {
        return drawEffects;
    }

    public BlockHarvester setDrawEffects(boolean drawEffects) {
        this.drawEffects = drawEffects;
        return this;
    }

    public boolean isDropXp() {
        return dropXp;
    }

    public EntityPlayer getXpTarget() {
        return xpTarget;
    }

    public BlockHarvester setXpTarget(EntityPlayer xpTarget) {
        this.xpTarget = xpTarget;
        return this;
    }

    public BlockHarvester setDropXp(boolean dropXp) {
        this.dropXp = dropXp;
        return this;
    }

    public boolean isIgnorePlayerCreative() {
        return ignorePlayerCreative;
    }

    public BlockHarvester setIgnorePlayerCreative(boolean ignorePlayerCreative) {
        this.ignorePlayerCreative = ignorePlayerCreative;
        return this;
    }

    public boolean isAddStats() {
        return addStats;
    }

    public BlockHarvester setAddStats(boolean addStats) {
        this.addStats = addStats;
        return this;
    }

    public boolean isAddExhaustion() {
        return addExhaustion;
    }

    public BlockHarvester setAddExhaustion(boolean addExhaustion) {
        this.addExhaustion = addExhaustion;
        return this;
    }

    public float getHardnessLimit() {
        return hardnessLimit;
    }

    public BlockHarvester setHardnessLimit(float hardnessLimit) {
        this.hardnessLimit = hardnessLimit;
        return this;
    }

    public int getDelayBeforePickup() {
        return delayBeforePickup;
    }

    public BlockHarvester setDelayBeforePickup(int delayBeforePickup) {
        this.delayBeforePickup = delayBeforePickup;
        return this;
    }

    public boolean isIgnoreTileEntity() {
        return ignoreTileEntity;
    }

    public BlockHarvester setIgnoreTileEntity(boolean ignoreTileEntity) {
        this.ignoreTileEntity = ignoreTileEntity;
        return this;
    }

    public boolean isIgnoreLiquids() {
        return ignoreLiquids;
    }

    public BlockHarvester setIgnoreLiquids(boolean ignoreLiquids) {
        this.ignoreLiquids = ignoreLiquids;
        return this;
    }

    public boolean isIgnoreCreative() {
        return ignoreCreative;
    }

    public BlockHarvester setIgnoreCreative(boolean ignoreCreative) {
        this.ignoreCreative = ignoreCreative;
        return this;
    }

    public int getHarvested() {
        return harvested;
    }

    public void setHarvested(int harvested) {
        this.harvested = harvested;
    }

    public int getHardHarvested() {
        return hardHarvested;
    }

    public void setHardHarvested(int hardHarvested) {
        this.hardHarvested = hardHarvested;
    }

    public boolean isIgnoreUnbreakable() {
        return ignoreUnbreakable;
    }

    public void setIgnoreUnbreakable(boolean ignoreUnbreakable) {
        this.ignoreUnbreakable = ignoreUnbreakable;
    }
}
