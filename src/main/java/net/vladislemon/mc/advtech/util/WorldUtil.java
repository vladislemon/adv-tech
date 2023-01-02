package net.vladislemon.mc.advtech.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;

/**
 * Created by slimon
 * on 15.06.2015.
 */
public class WorldUtil {

    public static boolean harvestBlock(World world, EntityPlayer player, int x, int y, int z, boolean silkTouch,
                                       int fortune) {
        Block block = world.getBlock(x, y, z);
        int blockMeta = world.getBlockMetadata(x, y, z);
        float hardness = block.getBlockHardness(world, x, y, z);
        ArrayList<ItemStack> drops = getBlockDrops(world, player, x, y, z, silkTouch, fortune);
        ForgeEventFactory.fireBlockHarvesting(drops, world, block, x, y, z, blockMeta, fortune, 1F, silkTouch, player);
        if(!player.capabilities.isCreativeMode) {
            createItemStacksInWorld(world, x, y, z, drops, 10);
        }
        if(!silkTouch) {
            block.dropXpOnBlockBreak(world, x, y, z, block.getExpDrop(world, blockMeta, fortune));
        }

        playBlockBreakSound(world, x, y, z, block);
        if(BaseHelper.isClient())
            Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(x, y, z, block, blockMeta);

        world.setBlockToAir(x, y, z);
        world.removeTileEntity(x, y, z);
        player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);
        player.addExhaustion(0.005F);

        return hardness != 0;
    }

    public static void playBlockBreakSound(World world, int x, int y, int z, Block block) {
        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, block.stepSound.getBreakSound(),
                (block.stepSound.getVolume() + 1F)/2F, block.stepSound.getPitch()*0.8F);
    }

    public static boolean removeBlock(World world, EntityPlayer player, int x, int y, int z, boolean ignoreTile) {
        WorldBlock wb = getBlockForMining(world, player, x, y, z, ignoreTile);
        if(wb != null) {
            world.setBlockToAir(x, y, z);
            return true;
        }
        return false;
    }

    public static boolean blockIsHard(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        float hardness = block.getBlockHardness(world, x, y, z);
        return hardness != 0;
    }

    public static ArrayList<ItemStack> getBlockDrops(World world, EntityPlayer player, int x, int y, int z,
                                                     boolean silkTouch, int fortune) {
        ArrayList<ItemStack> drops;
        Block block = world.getBlock(x, y, z);
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

    public static void createItemStackInWorld(World world, int x, int y, int z, ItemStack stack,
                                              int delayBeforePickup) {
        if(BaseHelper.isServer()) {
            if (stack != null) {
                EntityItem entity = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack);
                entity.delayBeforeCanPickup = delayBeforePickup;
                world.spawnEntityInWorld(entity);
            }
        }
    }

    public static void createItemStacksInWorld(World world, int x, int y, int z, ArrayList<ItemStack> stacks,
                                               int delayBeforePickup) {
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

    public static boolean hasBlockTileEntity(World world, int x, int y, int z, Block block) {
        int blockMeta = world.getBlockMetadata(x, y, z);
        return block.hasTileEntity(blockMeta);
    }

    public static WorldBlock getBlockForMining(World world, EntityPlayer player, int x, int y, int z,
                                               boolean ignoreTile) {
        Block block = world.getBlock(x, y, z);
        int blockMeta = world.getBlockMetadata(x, y, z);
        float hardness = block.getBlockHardness(world, x, y, z);
        if(world.canMineBlock(player, x, y, z) && (ignoreTile || !block.hasTileEntity(blockMeta)) && hardness >= 0
                && !block.getMaterial().isLiquid() && !block.equals(Blocks.air)) {
            return new WorldBlock(x, y, z);
        }
        return null;
    }

    public static ArrayList<WorldBlock> getBlocksSquare(World world, EntityPlayer player, int x, int y, int z,
                                                        int side, int radius, boolean ignoreTile) {
        ArrayList<WorldBlock> blocks = new ArrayList<WorldBlock>();
        blocks.add(getBlockForMining(world, player, x, y, z, ignoreTile));
        if(!blockIsHard(world, x, y, z)) {
            return blocks;
        }

        int dX = radius;
        int dY = radius;
        int dZ = radius;
        if(side / 2 == 0) dY = 0;
        if(side / 2 == 1) dZ = 0;
        if(side / 2 == 2) dX = 0;

        WorldBlock wb;

        if(dX == 0) {
            for(int k = y - dY; k <= y + dY; k++) {
                for(int n = z - dZ; n <= z + dZ; n++) {
                    if((k != y || n != z) && (wb = getBlockForMining(world, player, x, k, n, ignoreTile)) != null)
                        blocks.add(wb);
                }
            }
        }
        if(dY == 0) {
            for(int i = x - dX; i <= x + dX; i++) {
                for(int n = z - dZ; n <= z + dZ; n++) {
                    if((i != x || n != z) && (wb = getBlockForMining(world, player, i, y, n, ignoreTile)) != null)
                        blocks.add(wb);
                }
            }
        }
        if(dZ == 0) {
            for(int i = x - dX; i <= x + dX; i++) {
                for(int k = y - dY; k <= y + dY; k++) {
                    if((i != x || k != y) && (wb = getBlockForMining(world, player, i, k, z, ignoreTile)) != null)
                        blocks.add(wb);
                }
            }
        }
        return blocks;
    }

    @Deprecated
    public static int mineBlocksSquare(World world, EntityPlayer player, int x, int y, int z, int side, int radius,
                                       boolean silkTouch, int fortune) {
        int blocks = 0;
        int dX = radius;
        int dY = radius;
        int dZ = radius;
        if(side / 2 == 0) dY = 0;
        if(side / 2 == 1) dZ = 0;
        if(side / 2 == 2) dX = 0;

        if(dX == 0) {
            for(int k = y - dY; k <= y + dY; k++) {
                for(int n = z - dZ; n <= z + dZ; n++) {
                    if(harvestBlock(world, player, x, k, n, silkTouch, fortune)) blocks++;
                }
            }
        }
        if(dY == 0) {
            for(int i = x - dX; i <= x + dX; i++) {
                for(int n = z - dZ; n <= z + dZ; n++) {
                    if(harvestBlock(world, player, i, y, n, silkTouch, fortune)) blocks++;
                }
            }
        }
        if(dZ == 0) {
            for(int i = x - dX; i <= x + dX; i++) {
                for(int k = y - dY; k <= y + dY; k++) {
                    if(harvestBlock(world, player, i, k, z, silkTouch, fortune)) blocks++;
                }
            }
        }
        return blocks;
    }

    public static WorldBlock getWorldBlockAtSide(int x, int y, int z, int side) {
        switch(side) {
            case 0: y--;
                break;
            case 1: y++;
                break;
            case 2: x--;
                break;
            case 3: x++;
                break;
            case 4: z--;
                break;
            case 5: z++;
                break;
        }
        return new WorldBlock(x, y, z);
    }

    public static ArrayList<WorldBlock> getSameBlocks(World world, int x, int y, int z, Block block, int meta) {
        return getSameBlocks(world, x, y, z, block, meta, new ArrayList<WorldBlock>());
    }

    private static ArrayList<WorldBlock> getSameBlocks(World world, int x, int y, int z, Block block, int meta,
                                                       ArrayList<WorldBlock> blocks) {
        ArrayList<WorldBlock> temp = new ArrayList<WorldBlock>(6);
        Block b;
        WorldBlock wb;
        int m;
        for(int i = 0; i < 6; i++) {
            wb = getWorldBlockAtSide(x, y, z, i);
            b = world.getBlock(wb.x, wb.y, wb.z);
            m = world.getBlockMetadata(wb.x, wb.y, wb.z);
            if(!blocks.contains(wb) && b.equals(block) && m == meta) {
                temp.add(wb);
                blocks.add(wb);
            }
        }
        for(WorldBlock aTemp : temp) {
            getSameBlocks(world, aTemp.x, aTemp.y, aTemp.z, block, meta, blocks);
        }
        return blocks;
    }

    public static ArrayList<WorldBlock> getSameBlocksWithOreDict(World world, int x, int y, int z, int[] oreIds,
                                                                 String[] contains) {
        ArrayList<ItemStack> needBlocks = new ArrayList<ItemStack>();
        if(oreIds != null && oreIds.length != 0) {
            String s;
            for (int id : oreIds) {
                s = OreDictionary.getOreName(id);
                for (String c : contains) {
                    if (s.contains(c)) {
                        needBlocks.addAll(OreDictionary.getOres(s));
                        break;
                    }
                }
            }
        }
        return getSameBlocks(world, x, y, z, needBlocks);
    }

    public static ArrayList<WorldBlock> getSameBlocksWithOreDict(World world, int x, int y, int z, String name) {
        ArrayList<ItemStack> needBlocks = OreDictionary.getOres(name);
        return getSameBlocks(world, x, y, z, needBlocks);
    }

    public static ArrayList<WorldBlock> getSameBlocks(World world, int x, int y, int z,
                                                      ArrayList<ItemStack> needBlocks) {
        ArrayList<WorldBlock> blocks = new ArrayList<WorldBlock>();
        blocks.add(new WorldBlock(x, y, z));
        if(needBlocks == null || needBlocks.size() == 0) {
            return blocks;
        }
        return getSameBlocks(world, x, y, z, needBlocks, blocks);
    }

    private static ArrayList<WorldBlock> getSameBlocks(World world, int x, int y, int z,
                                                       ArrayList<ItemStack> needBlocks,
                                                       ArrayList<WorldBlock> blocks) {
        ArrayList<WorldBlock> temp = new ArrayList<WorldBlock>(6);
        Block b;
        WorldBlock wb;
        int m;
        for(int i = 0; i < 6; i++) {
            if(blocks.size() >= 512)
                return blocks;
            wb = getWorldBlockAtSide(x, y, z, i);
            b = world.getBlock(wb.x, wb.y, wb.z);
            m = world.getBlockMetadata(wb.x, wb.y, wb.z);
            if(!blocks.contains(wb)) {
                for(ItemStack need : needBlocks) {
                    if(Block.getBlockFromItem(need.getItem()).equals(b) && need.getItemDamage() == m) {
                        temp.add(wb);
                        blocks.add(wb);
                    }
                }
            }
        }
        for (WorldBlock aTemp : temp) {
            getSameBlocks(world, aTemp.x, aTemp.y, aTemp.z, needBlocks, blocks);
        }
        return blocks;
    }

    public static int mineBlocks(World world, EntityPlayer player, ArrayList<WorldBlock> blocks, boolean silkTouch,
                                 int fortune, boolean toInventory) {
        return mineBlocks(world, player, blocks, blocks.size(), silkTouch, fortune, toInventory);
    }

    public static int mineBlocks(World world, EntityPlayer player, ArrayList<WorldBlock> blocks, int max,
                                 boolean silkTouch, int fortune, boolean toInventory) {
        if(toInventory)
            return mineBlocksToInventory(world, player, blocks, max, silkTouch, fortune);
        return mineBlocksToWorld(world, player, blocks, max, silkTouch, fortune);
    }

    public static int mineBlocksToWorld(World world, EntityPlayer player, ArrayList<WorldBlock> blocks,
                                        boolean silkTouch, int fortune) {
        return mineBlocksToWorld(world, player, blocks, blocks.size(), silkTouch, fortune);
    }

    public static int mineBlocksToWorld(World world, EntityPlayer player, ArrayList<WorldBlock> blocks, int max,
                                        boolean silkTouch, int fortune) {
        int mined = 0;
        WorldBlock block;
        for(int i = 0; i < Math.min(blocks.size(), max); i++) {
            block = blocks.get(i);
            if(harvestBlock(world, player, block.x, block.y, block.z, silkTouch, fortune))
                mined++;
        }
        return mined;
    }

    public static int mineBlocksToInventory(World world, EntityPlayer player, ArrayList<WorldBlock> blocks,
                                            boolean silkTouch, int fortune) {
        return mineBlocksToInventory(world, player, blocks, blocks.size(), silkTouch, fortune);
    }

    public static int mineBlocksToInventory(World world, EntityPlayer player, ArrayList<WorldBlock> blocks, int max,
                                            boolean silkTouch, int fortune) {
        int mined = 0;
        WorldBlock wb;
        for(int i = 0; i < Math.min(blocks.size(), max); i++) {
            wb = blocks.get(i);
            if(wb != null && mineBlockToInventory(world, player, wb, silkTouch, fortune))
                mined++;
        }
        return mined;
    }

    public static boolean mineBlockToInventory(World world, EntityPlayer player, WorldBlock wb, boolean silkTouch,
                                               int fortune) {
        Block block = world.getBlock(wb.x, wb.y, wb.z);
        int blockMeta = world.getBlockMetadata(wb.x, wb.y, wb.z);
        ArrayList<ItemStack> drops = getBlockDrops(world, player, wb.x, wb.y, wb.z, silkTouch, fortune);
        ForgeEventFactory.fireBlockHarvesting(drops, world, block, wb.x, wb.y, wb.z, blockMeta, fortune, 1F,
                silkTouch, player);
        if(!silkTouch) {
            int exp = block.getExpDrop(world, blockMeta, fortune);
            if(exp > 0) {
                player.xpCooldown = 2;
                world.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((float) (Math.random() - Math.random()) * 0.7F + 1.8F));
                player.addExperience(exp);
            }
        }
        boolean isHard = blockIsHard(world, wb.x, wb.y, wb.z);

        playBlockBreakSound(world, wb.x, wb.y, wb.z, block);
        if(BaseHelper.isClient())
            Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(wb.x, wb.y, wb.z, block, blockMeta);

        world.setBlockToAir(wb.x, wb.y, wb.z);
        world.removeTileEntity(wb.x, wb.y, wb.z);
        player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(world.getBlock(wb.x, wb.y, wb.z))], 1);
        player.addExhaustion(0.005F);
        if(drops != null && !player.capabilities.isCreativeMode) {
            drops = PlayerHelper.addToInventory(player, drops);
            createItemStacksInWorld(world, wb.x, wb.y, wb.z, drops, 2);
        }
        return isHard;
    }

    public static int getBlockSideAtPlayerLook(World world, EntityPlayer player) {
        float distance = 4.5F;
        if(player.capabilities.isCreativeMode) distance = 5.0F;
        MovingObjectPosition mop = RayTrace.rayTraceBlockFromPlayer(world, player, distance, false);
        if (mop != null) {
            return mop.sideHit;
        }
        return -1;
    }

    public static boolean createLightingBolt(World world, int x, int y, int z, boolean createFire) {
        if(world.canBlockSeeTheSky(x, y, z)) {
            world.addWeatherEffect(new EntityLightningBolt(world, x, y, z));
            if(createFire) world.setBlock(x, y, z, Blocks.fire);
            return true;
        }
        return false;
    }

    public static ArrayList<EntityItem> getItemEntitiesAroundEntity(World world, Entity entity, double radius) {
        ArrayList<EntityItem> entities = new ArrayList<EntityItem>();
        EntityItem entityItem;
        for(Object obj : world.loadedEntityList) {
            if(obj instanceof EntityItem) {
                entityItem = (EntityItem) obj;
                if(entityItem.getDistanceSqToEntity(entity) <= radius*radius)
                    entities.add(entityItem);
            }
        }
        return entities;
    }

    public static ArrayList<Entity> getItemAndXPEntitiesAroundEntity(World world, Entity entity, double radius) {
        ArrayList<Entity> entities = new ArrayList<Entity>();
        Entity entityItem;
        for(Object obj : world.loadedEntityList) {
            if(obj instanceof EntityItem || obj instanceof EntityXPOrb) {
                entityItem = (Entity) obj;
                if(entityItem.getDistanceSqToEntity(entity) <= radius*radius)
                    entities.add(entityItem);
            }
        }
        return entities;
    }

    public static ArrayList<EntityItem> getItemEntitiesAround(World world, double x, double y, double z, double radius) {
        ArrayList<EntityItem> entities = new ArrayList<EntityItem>();
        EntityItem entityItem;
        for(Object obj : world.loadedEntityList) {
            if(obj instanceof EntityItem) {
                entityItem = (EntityItem) obj;
                if(entityItem.getDistanceSq(x, y, z) <= radius*radius)
                    entities.add(entityItem);
            }
        }
        return entities;
    }

    public static ArrayList<WorldBlock> getBlocksInChunkWithOreDict(World world, int worldX, int worldZ) {
        //BaseHelper.printToConsole("worldX: " + worldX + "; worldZ: " + worldZ);
        //BaseHelper.printToConsole("chunkX: " + (worldX >> 4) + "; chunkZ: " + (worldZ >> 4));
        int chunkX = worldX >> 4, chunkZ = worldZ >> 4;
        int startX = chunkX << 4, startZ = chunkZ << 4;
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        //world.getChunkFromBlockCoords()
        ArrayList<WorldBlock> blocks = new ArrayList<WorldBlock>();
        Block block;
        int blockMeta;
        for(int y = world.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    //BaseHelper.printToConsole("x: " + x + "; z: " + z + "; y: " + y);
                    //block = chunk.getBlock(x, y, z);
                    //blockMeta = chunk.getBlockMetadata(x, y, z);
                    //int[] ids = OreDictionary.getOreIDs(new ItemStack(block, blockMeta));
                    /*if(ids != null && ids.length > 0 && OreDictionary.getOreName(ids[0]).startsWith("ore")) {
                        blocks.add(new WorldBlock(chunk.xPosition << 4 + x, y, chunk.zPosition << 4 + z));
                    }*/
                    block = chunk.getBlock(x, y, z);
                    if(block != Blocks.air) {
                        blocks.add(new WorldBlock(startX + x, y, startZ + z));
                    }
                    /*if(BaseHelper.stackContains(new ItemStack(block, blockMeta), needBlocks)) {
                        blocks.add(new WorldBlock(chunk.xPosition << 4 + x, y, chunk.zPosition << 4 + z));
                    }*/
                }
            }
        }
        return blocks;
    }
}
