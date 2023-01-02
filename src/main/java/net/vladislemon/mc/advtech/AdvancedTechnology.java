package net.vladislemon.mc.advtech;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.vladislemon.mc.advtech.core.item.ItemLitRedstone;
import net.vladislemon.mc.advtech.init.Config;
import net.vladislemon.mc.advtech.handler.InputHandler;
import net.vladislemon.mc.advtech.proxy.Common;

/**
 * Created by slimon
 * on 07.06.2015.
 */

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.7.10]")
public class AdvancedTechnology {

    @Mod.Instance("AdvancedTechnology")
    public static AdvancedTechnology instance;

    @SidedProxy(clientSide = Tags.GROUPNAME + ".proxy.Client", serverSide = Tags.GROUPNAME + ".proxy.Common")
    public static Common proxy;

    public static SimpleNetworkWrapper network;
    //private static final String channel0 = "AT_channel_0";
    public static InputHandler inputHandler;

    public CreativeTabs creativeTab;
    public Item creativeTabItem;

    public AdvancedTechnology() {
        instance = this;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        /*network = NetworkRegistry.INSTANCE.newSimpleChannel(channel0);
        network.registerMessage(TestMessage.Handler.class, TestMessage.class, 0, Side.SERVER);

        EntityRegistry.registerModEntity(EntityLaser.class, "Laser Beam", modEntityID++, this, 64, 10, true);

        if(BaseHelper.isClient()) {
            RenderingRegistry.registerEntityRenderingHandler(EntityLaser.class, new LaserRenderer());
        }*/
        Constants.CONFIG_DIR = event.getSuggestedConfigurationFile().getParent();
        proxy.initConfig(event.getSuggestedConfigurationFile());
        if(Config.instance.isModEnabled()) {
            proxy.preInit(event);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if(Config.instance.isModEnabled()) {
            proxy.init(event);
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if(Config.instance.isModEnabled()) {
            proxy.postInit(event);
        }
        Config.instance.save();



        //=======================
        //IC2.seasonal = false;
    }

    @Deprecated
    private void fixRedstone() {
        Item itemLitRedstone = Item.getItemFromBlock(Blocks.lit_redstone_ore);
        if(itemLitRedstone == null)
            itemLitRedstone = new ItemLitRedstone();
        if(!OreDictionary.getOres("oreRedstone").contains(new ItemStack(itemLitRedstone)))
            OreDictionary.registerOre("oreRedstone", itemLitRedstone);
    }
}
