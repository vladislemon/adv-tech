package net.vladislemon.mc.advtech.core;

import net.vladislemon.mc.advtech.core.item.*;
import net.vladislemon.mc.advtech.core.item.armor.TestArmor;
import net.vladislemon.mc.advtech.core.item.tool.CoolMiningTool;
import net.vladislemon.mc.advtech.core.item.tool.UltimateDrill;
import net.vladislemon.mc.advtech.init.ArmorItem;

import java.util.HashMap;

/**
 * Created by slimon
 * on 07.06.2015.
 */
public class ItemsBlocks {

    /**
     * fields below will be initialized in ItemsBlocksInitializer
     */


    public static ItemComponent upgradeMining3x3;
    public static ItemComponent upgradeMiningOres;
    public static ItemComponent upgradePickupItems;

    /*@Dependencies({
            @Dependency(type = "mod", name = "IC2", version = "2.2.732-experimental")
    })*/
    public static UltimateDrill ultimateDrill;

    public static Electromagnet electromagnet;

    public static Telemagnet telemagnet;

    public static OreDictConverter oreDictConverter;
    public static CoolMiningTool coolMiningTool;

    @ArmorItem(nameOfSet = "testArmor", type = ArmorItem.ArmorType.HELMET)
    public static TestArmor testArmorHelmet;
    @ArmorItem(nameOfSet = "testArmor", type = ArmorItem.ArmorType.CHESTPLATE)
    public static TestArmor testArmorChestplate;
    @ArmorItem(nameOfSet = "testArmor", type = ArmorItem.ArmorType.LEGGINS)
    public static TestArmor testArmorLeggins;
    @ArmorItem(nameOfSet = "testArmor", type = ArmorItem.ArmorType.BOOTS)
    public static TestArmor testArmorBoots;

    public static UniversalCharger universalCharger;


    //public static EnergyItem energyItem;
    //public static TestItem testItem;
    //public static TestEnergyItem testEnergyItem;


    //=== state mapping
    private static final HashMap<String, Boolean> stateMap = new HashMap<String, Boolean>();

    public static void setItemState(String name, boolean state) {
        stateMap.put(name, state);
    }

    public static boolean isItemActive(String name) {
        return stateMap.containsKey(name) && stateMap.get(name);
    }
}