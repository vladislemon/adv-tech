package net.vladislemon.mc.advtech.core.recipes;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.item.IC2Items;
import ic2.core.AdvRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.vladislemon.mc.advtech.Constants;
import net.vladislemon.mc.advtech.core.ItemsBlocks;
import net.vladislemon.mc.advtech.init.Config;
import net.vladislemon.mc.advtech.util.RecipeConfig;

import java.io.File;

/**
 * Created by Notezway on 22.12.2015.
 */
public class Recipes {

    public static void registerRecipes() {
        RecipeSorter.register("AT Tool Upgrades", ImprovableToolRecipes.class, RecipeSorter.Category.SHAPELESS, "");
        GameRegistry.addRecipe(new ImprovableToolRecipes());

        RecipeConfig config = new RecipeConfig(new File(Constants.CONFIG_DIR + File.separator + "AT_Recipes.txt"));
        config.load();

        if (Constants.IC2_LOADED && Config.instance.useIC2Recipes()) {
            //Ultimate Drill
            if (ItemsBlocks.isItemActive("ultimateDrill")) {
                ItemStack ultimateDrill = ItemsBlocks.ultimateDrill.getItemStack(ItemsBlocks.ultimateDrill, 0);
                AdvRecipe.addAndRegister(ultimateDrill, "OIO", "IDI", "VLV",
                        'O', IC2Items.getItem("overclockerUpgrade").copy(),
                        'I', IC2Items.getItem("iridiumPlate").copy(),
                        'D', new ItemStack(IC2Items.getItem("iridiumDrill").copy().getItem(), 1, OreDictionary.WILDCARD_VALUE),
                        'V', new ItemStack(IC2Items.getItem("reactorVentDiamond").copy().getItem(), 1, OreDictionary.WILDCARD_VALUE),
                        'L', new ItemStack(IC2Items.getItem("lapotronCrystal").copy().getItem(), 1, OreDictionary.WILDCARD_VALUE));
            }

            //Upgrade 3x3
            if (ItemsBlocks.isItemActive("upgradeMining3x3")) {
                ItemStack upgradeMining3x3 = new ItemStack(ItemsBlocks.upgradeMining3x3);
                AdvRecipe.addAndRegister(upgradeMining3x3, " R ", "MCM", "TST",
                        'R', new ItemStack(IC2Items.getItem("steelrotor").copy().getItem(), 1, OreDictionary.WILDCARD_VALUE),
                        'M', IC2Items.getItem("elemotor").copy(),
                        'C', IC2Items.getItem("advancedCircuit").copy(),
                        'T', IC2Items.getItem("transformerUpgrade").copy(),
                        'S', new ItemStack(IC2Items.getItem("odScanner").copy().getItem(), 1, OreDictionary.WILDCARD_VALUE));
            }

            //Upgrade ores
            if (ItemsBlocks.isItemActive("upgradeMiningOres")) {
                ItemStack upgradeMiningOres = new ItemStack(ItemsBlocks.upgradeMiningOres);
                AdvRecipe.addAndRegister(upgradeMiningOres, " L ", "VCV", "TST",
                        'L', new ItemStack(IC2Items.getItem("miningLaser").copy().getItem(), 1, OreDictionary.WILDCARD_VALUE),
                        'V', IC2Items.getItem("reactorVentDiamond").copy(),
                        'C', new ItemStack(IC2Items.getItem("reactorVentDiamond").copy().getItem(), 1, OreDictionary.WILDCARD_VALUE),
                        'T', IC2Items.getItem("transformerUpgrade").copy(),
                        'S', new ItemStack(IC2Items.getItem("ovScanner").copy().getItem(), 1, OreDictionary.WILDCARD_VALUE));
            }

            //Upgrade pickup
            if (ItemsBlocks.isItemActive("upgradePickupItems")) {
                ItemStack upgradePickupItems = new ItemStack(ItemsBlocks.upgradePickupItems);
                AdvRecipe.addAndRegister(upgradePickupItems, " c ", "RCR", "TtT",
                        'c', IC2Items.getItem("coil").copy(),
                        'R', IC2Items.getItem("casingadviron").copy(),
                        'C', IC2Items.getItem("advancedCircuit").copy(),
                        'T', IC2Items.getItem("transformerUpgrade").copy(),
                        't', IC2Items.getItem("teleporter").copy());
            }

            //Electromagnet
            if (ItemsBlocks.isItemActive("electromagnet")) {
                ItemStack electromagnet = ItemsBlocks.electromagnet.getItemStack(ItemsBlocks.electromagnet, 0);
                AdvRecipe.addAndRegister(electromagnet, "ccc", "ICI", "IBI",
                        'c', IC2Items.getItem("coil").copy(),
                        'I', IC2Items.getItem("casingiron").copy(),
                        'C', IC2Items.getItem("electronicCircuit").copy(),
                        'B', new ItemStack(IC2Items.getItem("advBattery").copy().getItem(), 1, OreDictionary.WILDCARD_VALUE));
            }

            //Telemagnet
            if (ItemsBlocks.isItemActive("telemagnet")) {
                ItemStack telemagnet = ItemsBlocks.telemagnet.getItemStack(ItemsBlocks.telemagnet, 0);
                AdvRecipe.addAndRegister(telemagnet, " P ", " E ", "GCG",
                        'P', new ItemStack(Items.ender_pearl),
                        'E', new ItemStack(ItemsBlocks.electromagnet, 1, OreDictionary.WILDCARD_VALUE),
                        'G', IC2Items.getItem("casinggold").copy(),
                        'C', new ItemStack(IC2Items.getItem("energyCrystal").copy().getItem(), 1, OreDictionary.WILDCARD_VALUE));
            }

            //Ore Converter
            if (ItemsBlocks.isItemActive("oreDictConverter")) {
                ItemStack oreDictConverter = new ItemStack(ItemsBlocks.oreDictConverter);
                AdvRecipe.addAndRegister(oreDictConverter, "IRI", "RER", "IRI",
                        'I', IC2Items.getItem("casingiron").copy(),
                        'R', new ItemStack(Items.redstone),
                        'E', new ItemStack(Items.ender_pearl));
            }

            //Universal Charger
            if (ItemsBlocks.isItemActive("universalCharger")) {
                ItemStack universalCharger = ItemsBlocks.universalCharger.getItemStack(ItemsBlocks.universalCharger, 0);
                AdvRecipe.addAndRegister(universalCharger, " T ", "TCT", " T ",
                        'T', IC2Items.getItem("transformerUpgrade").copy(),
                        'C', new ItemStack(IC2Items.getItem("chargingLapotronCrystal").copy().getItem(), 1, OreDictionary.WILDCARD_VALUE));
            }
        } else {

            //Ultimate Drill
            if (ItemsBlocks.isItemActive("ultimateDrill")) {
                ItemStack ultimateDrill = ItemsBlocks.ultimateDrill.getItemStack(ItemsBlocks.ultimateDrill, 0);
                parseAndRegister(ultimateDrill, config.getString("ultimateDrill",
                        "IC2:upgradeModule,IC2:itemPartIridium,IC2:upgradeModule,IC2:itemPartIridium,IC2:itemToolIridiumDrill:*,IC2:itemPartIridium,IC2:reactorVentDiamond:*,IC2:itemBatLamaCrystal:*,IC2:reactorVentDiamond:*"));
            }

            //Upgrade 3x3
            if (ItemsBlocks.isItemActive("upgradeMining3x3")) {
                ItemStack upgradeMining3x3 = new ItemStack(ItemsBlocks.upgradeMining3x3);
                parseAndRegister(upgradeMining3x3, config.getString("upgradeMining3x3",
                        ",IC2:itemsteelrotor,,IC2:itemRecipePart:1,IC2:itemPartCircuitAdv,IC2:itemRecipePart:1,IC2:upgradeModule:1,IC2:itemScanner:*,IC2:upgradeModule:1"));
            }

            //Upgrade ores
            if (ItemsBlocks.isItemActive("upgradeMiningOres")) {
                ItemStack upgradeMiningOres = new ItemStack(ItemsBlocks.upgradeMiningOres);
                parseAndRegister(upgradeMiningOres, config.getString("upgradeMiningOres",
                        ",IC2:itemToolMiningLaser:*,,IC2:reactorVentDiamond:*,IC2:itemPartCircuitAdv,IC2:reactorVentDiamond:*,IC2:upgradeModule:1,IC2:itemScannerAdv:*,IC2:upgradeModule:1"));
            }

            //Upgrade pickup
            if (ItemsBlocks.isItemActive("upgradePickupItems")) {
                ItemStack upgradePickupItems = new ItemStack(ItemsBlocks.upgradePickupItems);
                parseAndRegister(upgradePickupItems, config.getString("upgradePickupItems",
                        ",IC2:itemRecipePart,,IC2:itemCasing:5,IC2:itemPartCircuitAdv,IC2:itemCasing:5,IC2:upgradeModule:1,IC2:blockMachine2,IC2:upgradeModule:1"));
            }

            //Electromagnet
            if (ItemsBlocks.isItemActive("electromagnet")) {
                ItemStack electromagnet = ItemsBlocks.electromagnet.getItemStack(ItemsBlocks.electromagnet, 0);
                parseAndRegister(electromagnet, config.getString("electromagnet",
                        "IC2:itemRecipePart,IC2:itemRecipePart,IC2:itemRecipePart,IC2:itemCasing:4,IC2:itemPartCircuit,IC2:itemCasing:4,IC2:itemCasing:4,IC2:itemAdvBat:*,IC2:itemCasing:4"));
            }

            //Telemagnet
            if (ItemsBlocks.isItemActive("telemagnet")) {
                ItemStack telemagnet = ItemsBlocks.telemagnet.getItemStack(ItemsBlocks.telemagnet, 0);
                parseAndRegister(telemagnet, config.getString("telemagnet",
                        ",minecraft:ender_pearl,,,AdvancedTechnology:electromagnet:*,,IC2:itemCasing:3,IC2:itemBatCrystal:*,IC2:itemCasing:3"));
            }

            //Ore Converter
            if (ItemsBlocks.isItemActive("oreDictConverter")) {
                ItemStack oreDictConverter = new ItemStack(ItemsBlocks.oreDictConverter);
                parseAndRegister(oreDictConverter, config.getString("oreDictConverter",
                        "IC2:itemCasing:4,minecraft:redstone,IC2:itemCasing:4,minecraft:redstone,minecraft:ender_pearl,minecraft:redstone,IC2:itemCasing:4,minecraft:redstone,IC2:itemCasing:4"));
            }
        }
        config.save();

    }

    private static void parseAndRegister(ItemStack result, String recipe) {
        String[] array = recipe.split(",");
        Item recipeItem;
        Object[] recipeArray;
        boolean flag, isOreDictName, globalFlag = false, globalOreDict = false, shapeless;
        String[] rawNameArray;
        String rawName, itemName, modId, metaString;
        shapeless = extractName(array[0]).equals("shapeless");
        int shift, count = 0, metadata;
        if (shapeless) {
            shift = 1;
            recipeArray = new Object[array.length - 1];
        } else {
            shift = 0;
            recipeArray = new Object[array.length * 2 + 3];
            recipeArray[0] = "012";
            recipeArray[1] = "345";
            recipeArray[2] = "678";
        }
        for (int i = shift; i < array.length; i++) {
            rawName = extractName(array[i]);
            flag = false;
            if (!rawName.isEmpty()) {
                rawNameArray = rawName.split(":");
                if (rawNameArray.length >= 2) {
                    modId = rawNameArray[0];
                    itemName = rawNameArray[1];
                    metadata = 0;
                    if (rawNameArray.length == 3) {
                        metaString = rawNameArray[2];
                        metadata = metaString.equals("*") ? OreDictionary.WILDCARD_VALUE : Integer.parseInt(metaString);
                    }
                    isOreDictName = modId.equals("ore");
                    globalOreDict |= isOreDictName;
                    recipeItem = isOreDictName ? null : GameRegistry.findItem(modId, itemName);
                    if ((isOreDictName && OreDictionary.doesOreNameExist(itemName)) || recipeItem != null) {
                        if (shapeless) {
                            recipeArray[i - 1] = isOreDictName ? itemName : new ItemStack(recipeItem, 1, metadata);
                        } else {
                            recipeArray[(i + 1) * 2 + 1] = (i + "").charAt(0);
                            recipeArray[(i + 1) * 2 + 2] = isOreDictName ? itemName : new ItemStack(recipeItem, 1, metadata);
                        }
                        count++;
                        globalFlag = flag = true;
                    }
                }
            }
            if (!flag) {
                if (shapeless) {
                    recipeArray[i - 1] = "";
                } else {
                    recipeArray[(i + 1) * 2 + 1] = '#';
                    recipeArray[(i + 1) * 2 + 2] = "";
                }
            }
        }
        if (globalFlag) {
            //System.out.println("Adding recipe for " + result.getDisplayName());
            if (shapeless) {
                Object[] oldRecipeArray = recipeArray;
                recipeArray = new Object[count];
                System.arraycopy(oldRecipeArray, 0, recipeArray, 0, count);
            }
            if (globalOreDict) {
                if (shapeless) {
                    //System.out.println("dict shapeless");
                    GameRegistry.addRecipe(new ShapelessOreRecipe(result, recipeArray));
                } else {
                    //System.out.println("dict shaped");
                    GameRegistry.addRecipe(new ShapedOreRecipe(result, recipeArray));
                }
            } else {
                if (shapeless) {
                    //System.out.println("shapeless");
                    GameRegistry.addShapelessRecipe(result, recipeArray);
                } else {
                    //System.out.println("shaped");
                    GameRegistry.addShapedRecipe(result, recipeArray);
                }
            }
        }
    }

    private static String extractName(String raw) {
        return raw.trim().replaceAll("\"", "");
    }
}
