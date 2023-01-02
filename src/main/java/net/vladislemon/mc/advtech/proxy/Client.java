package net.vladislemon.mc.advtech.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.vladislemon.mc.advtech.AdvancedTechnology;

/**
 * Created by user on 12/16/2016.
 */
public class Client extends Common {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        FMLCommonHandler.instance().bus().register(AdvancedTechnology.inputHandler);
        AdvancedTechnology.inputHandler.initKeys();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);

        //RenderingRegistry.registerEntityRenderingHandler(EntityLaser.class, new LaserRenderer());
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
}
