package net.vladislemon.mc.advtech.core.item.tool;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import net.vladislemon.mc.advtech.util.BaseHelper;
import net.vladislemon.mc.advtech.util.NBTUtil;

import java.util.*;

/**
 * Created by Notezway on 15.12.2015.
 */
public abstract class ImprovableTool extends BaseTool {

    private EnumSet<ToolMode> possibleModes;
    private Map<ToolMode, ItemStack> craftRequirements;
    private ToolMode[] defaultModes;
    private final String MODE = "mode";
    private final String MODES = "modes";

    public ImprovableTool(ToolMaterial material, Set<Block> mineableBlocks, float vsEntityDamage, ToolMode... defaultModes) {
        super(material, mineableBlocks, vsEntityDamage);
        setPossibleModes();
        setCraftRequirements(new HashMap<ToolMode, ItemStack>());
        setDefaultModes(defaultModes);
    }

    public boolean canCraft(ItemStack stack, ItemStack... components) {
        if(components.length < 1) return false;
        ArrayList<ItemStack> usedItems = new ArrayList<ItemStack>();
        for(ToolMode mode : getModes(stack)) {
            usedItems.add(this.craftRequirements.get(mode));
        }
        if(BaseHelper.hasDuplicates(false, false, components)) return false;
        for (ItemStack component : components) {
            if (BaseHelper.stackContains(component, usedItems) || !BaseHelper.stackContains(component, craftRequirements.values())) {
                return false;
            }
        }
        return true;
    }

    public ItemStack onCrafting(ItemStack stack, ItemStack... components) {
        ItemStack newStack = stack.copy();
        if(canCraft(stack, components)) {
            upgradeTool(newStack, getActivatedModes(components));
        }
        return newStack;
    }

    private ToolMode[] getActivatedModes(ItemStack... components) {
        ArrayList<ToolMode> activated = new ArrayList<ToolMode>();
        ArrayList<ToolMode> modes = new ArrayList<ToolMode>();
        modes.addAll(craftRequirements.keySet());
        ItemStack req;
        for(ToolMode mode : modes) {
            req = craftRequirements.get(mode);
            if(BaseHelper.stackContains(req, components)) {
                activated.add(mode);
            }
        }
        ToolMode[] ret = activated.toArray(new ToolMode[activated.size()]);
        return ret.length == 0 ? null : ret;
    }

    public boolean upgradeTool(ItemStack stack, ToolMode... newModes) {
        boolean success = false;
        if(newModes != null) {
            for (ToolMode mode : newModes) {
                if (upgradeTool(stack, mode)) {
                    success = true;
                }
            }
        }
        return success;
    }

    public boolean upgradeTool(ItemStack stack, ToolMode newMode) {
        if(this.possibleModes.contains(newMode)) {
            byte[] currentModes = getModesId(stack);
            byte id = (byte) newMode.ordinal();
            if(ArrayUtils.contains(currentModes, id)) {
                return false;
            }
            byte[] newModes = new byte[currentModes.length + 1];
            System.arraycopy(currentModes, 0, newModes, 0, currentModes.length);
            newModes[currentModes.length] = id;
            setModes(stack, newModes);
            return true;
        }
        return false;
    }

    public boolean isModeAvailable(ItemStack stack, ToolMode mode) {
        for(ToolMode current : getModes(stack)) {
            if(current.equals(mode)) return true;
        }
        return false;
    }

    public ToolMode[] getModes(ItemStack stack) {
        return convert(getModesId(stack));
    }

    public ToolMode[] getNonDefaultModes(ItemStack stack) {
        ArrayList<ToolMode> modes = new ArrayList<ToolMode>();
        for(ToolMode mode : getModes(stack)) {
            if(!ArrayUtils.contains(this.defaultModes, mode)) {
                modes.add(mode);
            }
        }
        return modes.toArray(new ToolMode[modes.size()]);
    }

    public ToolMode[] convert(byte[] modes) {
        ToolMode[] converted = new ToolMode[modes.length];
        for(int i = 0; i < modes.length; i++) {
            converted[i] = ToolMode.values()[modes[i]];
        }
        return converted;
    }

    public boolean nextMode(ItemStack stack, int step) {
        byte currentId = getModeId(stack);
        byte[] modes = getModesId(stack);
        int choose;
        byte id;
        ToolMode mode;
        //BaseHelper.printToConsole("Current name: " + currentId);
        //BaseHelper.printToConsole("IDS: " + Arrays.toString(modes));
        choose = ArrayUtils.indexOf(modes, currentId);
        for (byte mode1 : modes) {
            choose += step;
            if (choose >= modes.length) choose = 0;
            if (choose < 0) choose = modes.length - 1;
            //BaseHelper.printToConsole("Choose: " + choose);
            id = modes[choose];
            mode = ToolMode.values()[id];
            if (!mode.alwaysActive) {
                setMode(stack, mode);
                return true;
            }
        }
        return false;
    }

    public byte[] getModesId(ItemStack stack) {
        return NBTUtil.readByteArray(stack, MODES);
    }

    public void setModes(ItemStack stack, byte[] modes) {
        NBTUtil.writeByteArray(stack, MODES, modes);
    }

    public ToolMode getMode(ItemStack stack) {
        return ToolMode.values()[NBTUtil.readByte(stack, MODE)];
    }

    public byte getModeId(ItemStack stack) {
        return NBTUtil.readByte(stack, MODE);
    }

    public void setMode(ItemStack stack, ToolMode mode) {
        if(possibleModes.contains(mode)) {
            NBTUtil.writeByte(stack, MODE, (byte) mode.ordinal());
        }
    }

    public EnumSet<ToolMode> getPossibleModes() {
        return possibleModes;
    }

    public void setPossibleModes(ToolMode... possibleModes) {
        this.possibleModes = EnumSet.noneOf(ToolMode.class);
        if(this.defaultModes != null) Collections.addAll(this.possibleModes, this.defaultModes);
        if(possibleModes != null) Collections.addAll(this.possibleModes, possibleModes);
    }

    public Map<ToolMode, ItemStack> getCraftRequirements() {
        return craftRequirements;
    }

    public void setCraftRequirements(Map<ToolMode, ItemStack> craftRequirements) {
        this.craftRequirements = new HashMap<ToolMode, ItemStack>();
        ArrayList<ToolMode> modes = new ArrayList<ToolMode>();
        modes.addAll(craftRequirements.keySet());
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for(ToolMode mode : modes) {
            items.add(craftRequirements.get(mode));
        }
        for(int i = 0; i < modes.size(); i++) {
            ToolMode mode = modes.get(i);
            if(this.possibleModes.contains(mode)) {
                this.craftRequirements.put(mode, items.get(i));
            }
        }
    }

    public ToolMode[] getDefaultModes() {
        return defaultModes;
    }

    public void setDefaultModes(ToolMode... defaultModes) {
        if(defaultModes == null || defaultModes.length < 1) {
            this.defaultModes = new ToolMode[] {ToolMode.BASE};
        } else {
            this.defaultModes = defaultModes;
        }
    }

    public ToolMode[] getNonDefaultPossibleModes() {
        ArrayList<ToolMode> modes = new ArrayList<ToolMode>();
        for(ToolMode mode : this.possibleModes) {
            if(!ArrayUtils.contains(this.defaultModes, mode)) {
                modes.add(mode);
            }
        }
        return modes.toArray(new ToolMode[modes.size()]);
    }

    protected void setDefaults(ItemStack stack) {
        byte[] ids = new byte[defaultModes.length];
        for(int i = 0; i < ids.length; i++) {
            ids[i] = (byte) defaultModes[i].ordinal();
        }
        setModes(stack, ids);
        setMode(stack, defaultModes[0]);
    }

    public ItemStack getItemStack() {
        ItemStack ret = new ItemStack(this);
        setDefaults(ret);
        return ret;
    }
}
