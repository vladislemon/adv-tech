package net.vladislemon.mc.advtech.init;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.vladislemon.mc.advtech.AdvancedTechnology;
import net.vladislemon.mc.advtech.Tags;
import net.vladislemon.mc.advtech.core.ItemsBlocks;
import net.vladislemon.mc.advtech.core.item.armor.BaseArmor;
import net.vladislemon.mc.advtech.util.BaseHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by slimon
 * on 07.06.2015.
 */
public class ItemsBlocksInitializer {

    private HashSet<Field> forPostInit = new HashSet<Field>();

    public void registerATItemsAndBlocks(int stage) {
        try {
            Field[] fields = stage == FIRST_STAGE ? ItemsBlocks.class.getFields() : forPostInit.toArray(new Field[]{});
            for (Field field : fields) {
                Class<?> type = field.getType();
                if (Item.class.isAssignableFrom(type) || Block.class.isAssignableFrom(type)) {
                    String name = field.getName();
                    boolean state = false;
                    Annotation[] annotations = field.isAnnotationPresent(Dependency.class) || field.isAnnotationPresent(Dependencies.class) ? field.getDeclaredAnnotations() : null;
                    if (isObjectNeedInit(name, getDependenciesFromAnnotations(annotations))) {
                        String unlocalizedName = Tags.MODID + "." + name;
                        String textureName = Tags.MODID + ":" + name;
                        Object obj = null;
                        boolean isArmorItem = field.isAnnotationPresent(ArmorItem.class);
                        ArmorItem armorItemAnnotation = null;
                        if (isArmorItem) {
                            armorItemAnnotation = field.getAnnotation(ArmorItem.class);
                            Constructor<?>[] constructors = type.getConstructors();
                            for (Constructor<?> constructor : constructors) {
                                Class<?>[] parameterTypes = constructor.getParameterTypes();
                                if (parameterTypes.length == 1 && parameterTypes[0].equals(int.class)) {
                                    obj = constructor.newInstance(armorItemAnnotation.type().ordinal());
                                    break;
                                }
                            }
                        } else {
                            obj = type.newInstance();
                        }
                        if (obj != null) {
                            field.set(null, obj);
                            if (obj instanceof Item) {
                                Item item = (Item) obj;
                                item.setUnlocalizedName(unlocalizedName);
                                item.setTextureName(textureName);
                                if (isArmorItem) {
                                    ((BaseArmor) item).setArmorTexture(Tags.MODID + ":textures/armor/" + armorItemAnnotation.nameOfSet() + "_" +
                                            (armorItemAnnotation.type() == ArmorItem.ArmorType.LEGGINS ? "2" : "1") + ".png");
                                }
                                if (BaseHelper.isClient()) {
                                    item.setCreativeTab(AdvancedTechnology.instance.creativeTab);
                                }
                                GameRegistry.registerItem(item, name);
                            } else if (obj instanceof Block) {
                                Block block = (Block) obj;
                                block.setBlockName(unlocalizedName);
                                block.setBlockTextureName(textureName);
                                if (BaseHelper.isClient()) {
                                    block.setCreativeTab(AdvancedTechnology.instance.creativeTab);
                                }
                                GameRegistry.registerBlock(block, name);
                            }
                            state = true;
                        }
                        forPostInit.remove(field);
                    } else if (stage != THIRD_STAGE) {
                        forPostInit.add(field);
                    }
                    ItemsBlocks.setItemState(name, state);
                }
            }
            if (stage == THIRD_STAGE) {
                forPostInit = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isObjectNeedInit(String name, Dependency[] dependencies) {
        boolean inConfigEnabled = Config.instance.isItemOrBlockEnabled(name);
        boolean dependencyPresent = true;
        if (inConfigEnabled) {
            for (Dependency dependency : dependencies) {
                String type = dependency.type();
                switch (type) {
                    case "mod":
                        boolean modLoaded = Loader.isModLoaded(dependency.name());
                        dependencyPresent &= modLoaded;
                        break;
                    case "modItem":
                        dependencyPresent &= ItemsBlocks.isItemActive(dependency.name());
                        break;
                    case "otherItem":
                        dependencyPresent &= GameRegistry.findItem(dependency.modid(), dependency.name()) != null
                                || GameRegistry.findBlock(dependency.modid(), dependency.name()) != null;
                        break;
                }
            }
        }
        return inConfigEnabled && dependencyPresent;
    }

    private Dependency[] getDependenciesFromAnnotations(Annotation[] annotations) {
        if (annotations == null) return new Dependency[]{};
        HashSet<Dependency> dependencies = new HashSet<Dependency>();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(Dependency.class)) {
                dependencies.add((Dependency) annotation);
            } else if (annotation.annotationType().equals(Dependencies.class)) {
                dependencies.addAll(Arrays.asList(((Dependencies) annotation).value()));
            }
        }
        return dependencies.toArray(new Dependency[]{});
    }

    public static final int FIRST_STAGE = 0, SECOND_STAGE = 1, THIRD_STAGE = 2;
}
