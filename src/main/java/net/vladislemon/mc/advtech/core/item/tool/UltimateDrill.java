package net.vladislemon.mc.advtech.core.item.tool;

import com.google.common.collect.ImmutableSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.oredict.OreDictionary;
import net.vladislemon.mc.advtech.AdvancedTechnology;
import net.vladislemon.mc.advtech.Constants;
import net.vladislemon.mc.advtech.core.ItemsBlocks;
import net.vladislemon.mc.advtech.core.Materials;
import net.vladislemon.mc.advtech.core.energy.EnergyManager;
import net.vladislemon.mc.advtech.core.item.IWheelControlled;
import net.vladislemon.mc.advtech.handler.InputHandler;
import net.vladislemon.mc.advtech.util.BaseHelper;
import net.vladislemon.mc.advtech.util.BlockHarvester;
import net.vladislemon.mc.advtech.util.WorldBlock;
import net.vladislemon.mc.advtech.util.WorldUtil;
import org.lwjgl.input.Keyboard;

import java.util.*;

/**
 * ===
 * Created by Notezway on 12.11.2015.
 */
public class UltimateDrill extends EnergyImprovableTool implements IWheelControlled {

    private final String[] containsForOreDict;
    private final Set<String> toolClasses;

    public UltimateDrill() {
        super(ToolMaterial.EMERALD,
                new HashSet<>(Arrays.asList(Blocks.redstone_ore, Blocks.lit_redstone_ore, Blocks.obsidian, Blocks.glowstone)),
                5);
        toolClasses = ImmutableSet.of("pickaxe", "shovel");
        containsForOreDict = new String[]{"ore", "glowstone"};

        setDefaultModes(ToolMode.BASE, ToolMode.LOW_POWER);
        setPossibleModes(ToolMode.MINING_3X3, ToolMode.MINING_ORES, ToolMode.PICKUP_ITEMS);

        Map<ToolMode, ItemStack> craftReq = new HashMap<ToolMode, ItemStack>();
        if (ItemsBlocks.isItemActive("upgradeMining3x3"))
            craftReq.put(ToolMode.MINING_3X3, ItemsBlocks.upgradeMining3x3.getNewItemStack());
        if (ItemsBlocks.isItemActive("upgradeMiningOres"))
            craftReq.put(ToolMode.MINING_ORES, ItemsBlocks.upgradeMiningOres.getNewItemStack());
        if (ItemsBlocks.isItemActive("upgradePickupItems"))
            craftReq.put(ToolMode.PICKUP_ITEMS, ItemsBlocks.upgradePickupItems.getNewItemStack());
        setCraftRequirements(craftReq);

        this.setHarvestLevel("pickaxe", 4);
        this.setHarvestLevel("showel", 4);
        this.efficiencyOnProperMaterial = 75F;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack itemStack, World world, Block block, int x, int y, int z,
                                    EntityLivingBase entityLivingBase) {
        return true;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        checkEnergy(stack);
        World world = player.getEntityWorld();
        Block block = world.getBlock(x, y, z);
        processMining(stack, world, block, x, y, z, player);
        checkEnergy(stack);
        return true;
    }

    private void processMining(ItemStack stack, World world, Block block, int x, int y, int z, EntityPlayer player) {
        ToolMode mode = getMode(stack);
        ArrayList<WorldBlock> blocks = null;
        boolean ignoreTileAfterFirst = false;

        if (mode.equals(ToolMode.BASE) || mode.equals(ToolMode.LOW_POWER)) {
            blocks = new ArrayList<WorldBlock>();
            blocks.add(WorldUtil.getBlockForMining(world, player, x, y, z, true));
        } else if (mode.equals(ToolMode.MINING_3X3)) {
            blocks = getBlocksFor3x3MiningMode(world, player, x, y, z, block);
            ignoreTileAfterFirst = true;
        } else if (mode.equals(ToolMode.MINING_ORES)) {
            blocks = getBlocksForOreMiningMode(world, x, y, z, block);
        }

        assert blocks != null;
        int count = blocks.size();
        double energyUsage = getEnergyUsage(mode);
        double needEnergy = count * energyUsage;
        double availableEnergy = EnergyManager.getEnergyEU(stack);

        if (needEnergy > availableEnergy) {
            count = Math.max(1, (int) Math.floor(availableEnergy / needEnergy * count));
            BaseHelper.messagePlayerLocalized(player, "notEnoughEnergy");
        }

        boolean silkTouch = isSilkTouchEnabled(stack);
        int fortune = getFortuneLevel(stack);
        //System.out.println(AdvancedTechnology.inputHandler.getActionState(player, InputHandler.Action.ALT_KEY));
        boolean toInventory = isModeAvailable(stack, ToolMode.PICKUP_ITEMS) && !AdvancedTechnology.inputHandler.getActionState(player, InputHandler.Action.ALT_KEY);

        BlockHarvester harvester = new BlockHarvester(world).setBlockLimit(Math.min(count, 64));
        harvester.setPlayer(player)
                .setDropTarget(toInventory ? player.inventory : null)
                .setXpTarget(player)
                .setAddExhaustion(false)
                .setIgnoreTileEntity(false)
                .setSilkTouch(silkTouch).setFortune(fortune).reset();
        harvester.harvestBlocksByPlayer(0, 0, blocks);
        if (ignoreTileAfterFirst) {
            harvester.setIgnoreTileEntity(true);
        }
        harvester.setDrawEffects(false).setPlaySound(false);
        harvester.harvestBlocksByPlayer(1, blocks.size() - 1, blocks);
        int minedBlocks = harvester.getHardHarvested();
        //System.out.println(minedBlocks);
        harvester.reset();

        useEnergy(stack, player, minedBlocks * energyUsage);
    }

    private ArrayList<WorldBlock> getBlocksFor3x3MiningMode(World world, EntityPlayer player, int x, int y, int z, Block block) {
        int side = WorldUtil.getBlockSideAtPlayerLook(world, player);
        int radius = 1;
        ArrayList<WorldBlock> blocks = new ArrayList<WorldBlock>();
        if (side == -1) {
            blocks.add(WorldUtil.getBlockForMining(world, player, x, y, z, false));
            return blocks;
        }
        if (WorldUtil.hasBlockTileEntity(world, x, y, z, block)) {
            blocks.add(WorldUtil.getBlockForMining(world, player, x, y, z, false));
        }
        blocks.addAll(WorldUtil.getBlocksSquare(world, player, x, y, z, side, radius, true));
        return blocks;
    }

    private ArrayList<WorldBlock> getBlocksForOreMiningMode(World world, int x, int y, int z, Block block) {
        int meta = world.getBlockMetadata(x, y, z);
        int[] oreIds = OreDictionary.getOreIDs(new ItemStack(block, 1, meta));
        return WorldUtil.getSameBlocksWithOreDict(world, x, y, z, oreIds, containsForOreDict);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
                             float xOffset, float yOffset, float zOffset) {
        checkEnergy(stack);
        if (!AdvancedTechnology.inputHandler.getActionState(player, InputHandler.Action.MODE_KEY) && !AdvancedTechnology.inputHandler.getActionState(player, InputHandler.Action.ALT_KEY)) {
            if (!AdvancedTechnology.inputHandler.getActionState(player, InputHandler.Action.SNEAK_KEY)) {
                return placeTorch(player, world, x, y, z, side, xOffset, yOffset, zOffset);
            } else {
                return placeBlock(player, world, x, y, z, side, xOffset, yOffset, zOffset);
            }
        }
        return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
    }

    private boolean placeTorch(EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
            ItemStack torchStack = player.inventory.mainInventory[i];
            if (torchStack != null && torchStack.getUnlocalizedName().toLowerCase(Locale.ENGLISH).contains("torch")) {
                Item item = torchStack.getItem();
                if (item instanceof ItemBlock) {
                    int oldMeta = torchStack.getItemDamage();
                    int oldSize = torchStack.stackSize;
                    boolean result = torchStack.tryPlaceItemIntoWorld(player, world, x, y, z, side, xOffset, yOffset, zOffset);
                    if (player.capabilities.isCreativeMode) {
                        torchStack.setItemDamage(oldMeta);
                        torchStack.stackSize = oldSize;
                    } else if (torchStack.stackSize <= 0) {
                        ForgeEventFactory.onPlayerDestroyItem(player, torchStack);
                        player.inventory.mainInventory[i] = null;
                    }
                    if (result) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean placeBlock(EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        int slotId = player.inventory.currentItem + 1;
        if (player.inventory.mainInventory.length > slotId) {
            ItemStack blockStack = player.inventory.mainInventory[slotId];
            if (blockStack != null && blockStack.getItem() instanceof ItemBlock) {
                int oldMeta = blockStack.getItemDamage();
                int oldSize = blockStack.stackSize;
                boolean result = blockStack.tryPlaceItemIntoWorld(player, world, x, y, z, side, xOffset, yOffset, zOffset);
                if (player.capabilities.isCreativeMode) {
                    blockStack.setItemDamage(oldMeta);
                    blockStack.stackSize = oldSize;
                } else if (blockStack.stackSize <= 0) {
                    ForgeEventFactory.onPlayerDestroyItem(player, blockStack);
                    player.inventory.mainInventory[slotId] = null;
                }
                return result;
            }
        }
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        updateEnchantMode(stack, player);
        checkEnergy(stack);
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public void onWheelUp(EntityPlayer player, ItemStack stack) {
        updateDigingMode(stack, player, -1);
    }

    @Override
    public void onWheelDown(EntityPlayer player, ItemStack stack) {
        updateDigingMode(stack, player, 1);
    }

    public void updateEnchantMode(ItemStack stack, EntityPlayer player) {
        String message = null;
        if (AdvancedTechnology.inputHandler.getActionState(player, InputHandler.Action.MODE_KEY)) {
            boolean enoughEnergy = EnergyManager.canUseItemEU(stack, getEnergyUsage(getMode(stack)));
            boolean silkTouch = isSilkTouchEnabled(stack);
            if (enoughEnergy) {
                message = EnumChatFormatting.GOLD + BaseHelper.getLocalizedString("Enchantment") + ": ";
                if (silkTouch) {
                    setFortune(stack, 4);
                    message += BaseHelper.getLocalizedString("Fortune");
                } else {
                    setSilkTouch(stack, 1);
                    message += BaseHelper.getLocalizedString("SilkTouch");
                }
            } else {
                removeEnchantments(stack);
                message = EnumChatFormatting.DARK_RED + BaseHelper.getLocalizedString("noEnergy");
            }
            message += EnumChatFormatting.RESET;
        }
        if (message != null) BaseHelper.messagePlayer(player, message);
    }

    private void updateDigingMode(ItemStack stack, EntityPlayer player, int step) {
        if (AdvancedTechnology.inputHandler.getActionState(player, InputHandler.Action.MODE_KEY) && step != 0) {
            nextMode(stack, step);
            String message = EnumChatFormatting.DARK_AQUA + BaseHelper.getLocalizedString("Mode") + ": " +
                    BaseHelper.getLocalizedString("mode." + getMode(stack).name()) +
                    EnumChatFormatting.RESET;
            BaseHelper.messagePlayer(player, message);
        }
    }

    private void checkEnergy(ItemStack stack) {
        double energy = EnergyManager.getEnergyEU(stack);
        if (energy < getEnergyUsage(getMode(stack))) {
            removeEnchantments(stack);
        } else {
            if (!isFortuneEnabled(stack) && !isSilkTouchEnabled(stack)) {
                setFortune(stack, 4);
            }
        }
    }

    public double getEnergyUsage(ToolMode mode) {
        switch (mode) {
            case BASE:
                return 250D;
            case LOW_POWER:
                return 50D;
            case MINING_3X3:
                return 350D;
            case MINING_ORES:
                return 500D;
            default:
                return 0D;
        }
    }

    public float getSpeed(ToolMode mode) {
        float max = this.efficiencyOnProperMaterial;
        switch (mode) {
            case BASE:
                return max;
            case LOW_POWER:
                return max * 0.20F;
            case MINING_3X3:
                return max * 0.4F;
            case MINING_ORES:
                return max * 0.3F;
            default:
                return 1F;
        }
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        ToolMode mode = getMode(stack);
        if (isEffective(stack, block, meta) && EnergyManager.canUseItemEU(stack, getEnergyUsage(mode))) {
            return getSpeed(mode);
        }
        return 1.0F;
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack stack) {
        return Materials.canBeMinedByDrill(block.getMaterial()) || mineableBlocks.contains(block);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        return super.getHarvestLevel(stack, toolClass);
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return toolClasses;
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack1, ItemStack stack2) {
        return false;
    }

    @Override
    public ItemStack getItemStack(Item item, double charge) {
        ItemStack ret = super.getItemStack(item, charge);
        if (charge >= getEnergyUsage(ToolMode.BASE)) {
            setFortune(ret, 4);
        }
        EnergyManager.setEnergyEU(ret, charge);
        return ret;
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        ItemStack upgraded = getItemStack(item, Double.MAX_VALUE);
        upgradeTool(upgraded, getNonDefaultPossibleModes());

        itemList.add(upgraded);

        //how to get green check mark
        /*try {
            itemList.getClass().getMethod("add", ItemStack.class).invoke(itemList, upgraded);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }*/

        super.getSubItems(item, tabs, itemList);
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        String line = EnumChatFormatting.DARK_AQUA +
                BaseHelper.getLocalizedString("Mode") + ": " +
                BaseHelper.getLocalizedString("mode." + getMode(stack).name()) +
                EnumChatFormatting.RESET;
        list.add(line);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            String s = EnumChatFormatting.YELLOW +
                    BaseHelper.getLocalizedString("installedUpgrades") +
                    ": [";
            ToolMode[] modes = getNonDefaultModes(stack);
            for (int i = 0; i < modes.length; i++) {
                s += BaseHelper.getLocalizedString("mode." + modes[i].name());
                if (i < modes.length - 1) {
                    s += ", ";
                }
            }
            s += "]" + EnumChatFormatting.RESET;
            list.add(s);
            s = EnumChatFormatting.YELLOW +
                    BaseHelper.getLocalizedString("perBlock") + ": " +
                    (Constants.IC2_LOADED ? ((int) getEnergyUsage(getMode(stack)) + " EU") : ((int) (getEnergyUsage(getMode(stack)) * Constants.EU_TO_RF_RATIO) + " RF")) +
                    EnumChatFormatting.RESET;
            list.add(s);
        } else {
            line = EnumChatFormatting.DARK_GRAY + "<shift>" + EnumChatFormatting.RESET;
            list.add(line);
        }
        super.addInformation(stack, player, list, b);
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public List<String> getHudInfo(ItemStack stack) {
        List<String> info = super.getHudInfo(stack);
        String line = "§3" + BaseHelper.getLocalizedString("Mode") + ": " + BaseHelper.getLocalizedString("mode." +
                getMode(stack).name()) + "§r";
        info.add(line.substring(1, line.length()-3));

        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            String s = BaseHelper.getLocalizedString("installedUpgrades") + ": [";
            ToolMode[] modes = getNonDefaultModes(stack);
            for(int i = 0; i < modes.length; i++) {
                s += BaseHelper.getLocalizedString("mode." + modes[i].name());
                if(i < modes.length - 1) {
                    s += ", ";
                }
            }
            s += "]";
            info.add(s);
        }
        return info;
    }*/

    @Override
    public double getDefaultEnergy() {
        return 0;
    }

    @Override
    public double getDefaultMaxEnergy() {
        return 1000000;
    }

    @Override
    public double getDefaultMaxTransfer() {
        return 3200;
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
