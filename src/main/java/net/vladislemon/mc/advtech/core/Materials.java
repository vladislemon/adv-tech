package net.vladislemon.mc.advtech.core;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

/**
 * Created by Notezway on 17.12.2015.
 */
public class Materials {

    private static Material[] drillMaterials = new Material[] {
            Material.rock,
            Material.iron,
            Material.ground,
            Material.grass,
            Material.ice,
            Material.glass,
            Material.anvil,
            Material.clay,
            Material.piston,
            Material.craftedSnow,
            Material.snow,
            Material.packedIce,
            Material.redstoneLight
    };

    public static boolean canBeMinedByDrill(Material material) {
        for(Material current : drillMaterials) {
            if(current.equals(material)) return true;
        }
        return false;
    }


    public static int[] testArmorDamageReduction = new int[] {3, 8, 6, 3};
    public static ItemArmor.ArmorMaterial testArmor = EnumHelper.addArmorMaterial("AT_base_armor", 21, testArmorDamageReduction, 15);
}
