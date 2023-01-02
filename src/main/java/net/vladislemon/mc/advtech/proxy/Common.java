package net.vladislemon.mc.advtech.proxy;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.vladislemon.mc.advtech.AdvancedTechnology;
import net.vladislemon.mc.advtech.Constants;
import net.vladislemon.mc.advtech.Tags;
import net.vladislemon.mc.advtech.core.ItemsBlocks;
import net.vladislemon.mc.advtech.core.recipes.Recipes;
import net.vladislemon.mc.advtech.init.Config;
import net.vladislemon.mc.advtech.init.ItemsBlocksInitializer;
import net.vladislemon.mc.advtech.network.InputMessage;
import net.vladislemon.mc.advtech.handler.InputHandler;

import java.io.File;

/**
 * Created by user on 12/16/2016.
 */
public class Common {

    private ItemsBlocksInitializer itemsBlocksInitializer;

    public void initConfig(File file) {
        Configuration configFile = new Configuration(file);
        new Config(configFile);
    }

    public void preInit(FMLPreInitializationEvent e) {
        AdvancedTechnology.network = NetworkRegistry.INSTANCE.newSimpleChannel("AT_channel");
        AdvancedTechnology.network.registerMessage(InputMessage.Handler.class, InputMessage.class, 0, Side.SERVER);
        AdvancedTechnology.inputHandler = new InputHandler();
    }

    public void init(FMLInitializationEvent e) {
        Constants.IC2_LOADED = Loader.isModLoaded("IC2");
        Constants.EU_TO_RF_RATIO = Config.instance.getProperty(4d, Constants.CONFIG_GENERAL, Tags.MODID, "EU_TO_RF_RATIO").getDouble();

        initCreativeTab();

        itemsBlocksInitializer = new ItemsBlocksInitializer();
        itemsBlocksInitializer.registerATItemsAndBlocks(ItemsBlocksInitializer.FIRST_STAGE);
        itemsBlocksInitializer.registerATItemsAndBlocks(ItemsBlocksInitializer.SECOND_STAGE);

        AdvancedTechnology.instance.creativeTabItem = ItemsBlocks.ultimateDrill;


        //Recipes.registerRecipes();
        //int entityId = 0;
        //EntityRegistry.registerModEntity(EntityLaser.class, "Laser Beam", entityId++, AdvancedTechnology.instance, 64, 10, true);
    }

    private void initCreativeTab() {
        AdvancedTechnology.instance.creativeTab = new CreativeTabs(Tags.MODID) {
            @Override
            public Item getTabIconItem() {
                return AdvancedTechnology.instance.creativeTabItem;
            }
        };
    }

    public void postInit(FMLPostInitializationEvent e) {
        itemsBlocksInitializer.registerATItemsAndBlocks(ItemsBlocksInitializer.THIRD_STAGE);
        itemsBlocksInitializer = null;
        Recipes.registerRecipes();
    }
}
