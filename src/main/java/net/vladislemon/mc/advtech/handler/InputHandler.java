package net.vladislemon.mc.advtech.handler;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.vladislemon.mc.advtech.AdvancedTechnology;
import net.vladislemon.mc.advtech.core.item.IWheelControlled;
import net.vladislemon.mc.advtech.network.InputMessage;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import net.vladislemon.mc.advtech.util.BaseHelper;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by slimon on 16-05-17.
 */
public class InputHandler {

    static KeyBinding modeKey;
    static KeyBinding altKey;

    HashMap<UUID, PlayerAction> playerMap = new HashMap<UUID, PlayerAction>();

    public void initKeys() {
        ClientRegistry.registerKeyBinding(modeKey = new KeyBinding("key.AdvancedTechnology.mode", Keyboard.KEY_M, "Advanced Technology"));
        ClientRegistry.registerKeyBinding(altKey = new KeyBinding("key.AdvancedTechnology.alt", Keyboard.KEY_LMENU, "Advanced Technology"));
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        //if(modeKey.isPressed() || altKey.isPressed()) {
            //Action action = modeKey.isPressed() ? Action.MODE_KEY : altKey.isPressed() ? Action.ALT_KEY : null;
        if(Minecraft.getMinecraft().thePlayer != null) {
            UUID playerId = Minecraft.getMinecraft().thePlayer.getUniqueID();
            updateAction(playerId, Action.SNEAK_KEY, Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode()));
            updateAction(playerId, Action.MODE_KEY, Keyboard.isKeyDown(modeKey.getKeyCode()));
            updateAction(playerId, Action.ALT_KEY, Keyboard.isKeyDown(altKey.getKeyCode()));
            updateAction(playerId, Action.JUMP_KEY, Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode()));
        }
        //}
    }

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        int dWheel = Mouse.getEventDWheel();
        if(Minecraft.getMinecraft().thePlayer != null && dWheel != 0) {
            UUID playerId = Minecraft.getMinecraft().thePlayer.getUniqueID();
            Action action = dWheel > 0 ? Action.WHEEL_UP : Action.WHEEL_DOWN;
            tryLockSlot(dWheel > 0 ? 1 : -1);
            updateAction(playerId, action, true);
        }
    }

    private void updateAction(UUID playerId, Action action, boolean state) {
        //simulate
        onServerInput(playerId, action, state);

        AdvancedTechnology.network.sendToServer(new InputMessage(playerId, action, state));
    }

    private void tryLockSlot(int d) {
        if(Keyboard.isKeyDown(modeKey.getKeyCode()) && Minecraft.getMinecraft().thePlayer.inventory != null) {
            int newItem = Minecraft.getMinecraft().thePlayer.inventory.currentItem;
            if(newItem == 8 && d > 0) {
                Minecraft.getMinecraft().thePlayer.inventory.currentItem = d - 1;
            }
            else if(newItem == 0 && d < 0) {
                Minecraft.getMinecraft().thePlayer.inventory.currentItem = 9 + d;
            }
            else {
                Minecraft.getMinecraft().thePlayer.inventory.currentItem += d;
            }
            //System.out.println(Minecraft.getMinecraft().thePlayer.inventory.currentItem);
            //System.out.println(newItem);
            if(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() == null ||
                    !(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItem() instanceof IWheelControlled)) {
                //System.out.println(2);
                Minecraft.getMinecraft().thePlayer.inventory.currentItem = newItem;
            }
        }
    }

    public void onServerInput(UUID playerId, Action action, boolean state) {
        /*System.out.println(playerId);
        System.out.println(action);
        System.out.println(state);*/
        if(BaseHelper.isServer()) {
            if (!playerMap.containsKey(playerId)) {
                playerMap.put(playerId, new PlayerAction());
            }
            playerMap.get(playerId).stateMap[action.ordinal()] = state;

            if (action == Action.WHEEL_UP || action == Action.WHEEL_DOWN) {
                for (Object obj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
                    EntityPlayer player = (EntityPlayer) obj;
                    if (player.getUniqueID().equals(playerId)) {
                        ItemStack stack = player.getCurrentEquippedItem();
                        if (stack != null && stack.getItem() instanceof IWheelControlled) {
                            if (action == Action.WHEEL_UP) {
                                ((IWheelControlled) stack.getItem()).onWheelUp(player, stack);
                            } else {
                                ((IWheelControlled) stack.getItem()).onWheelDown(player, stack);
                            }
                        }
                    }
                }
            } else if (state) {
                playerMap.get(playerId).stateMap[Action.WHEEL_UP.ordinal()] = false;
                playerMap.get(playerId).stateMap[Action.WHEEL_DOWN.ordinal()] = false;
            }
        }
    }

    public boolean getActionState(EntityPlayer player, Action action) {
        return playerMap.containsKey(player.getUniqueID()) && playerMap.get(player.getUniqueID()).stateMap[action.ordinal()];
    }

    public boolean getActionStateAndDisable(EntityPlayer player, Action action) {
        boolean[] map = playerMap.get(player.getUniqueID()).stateMap;
        boolean value = map[action.ordinal()];
        map[action.ordinal()] = false;
        return value;
    }

    public enum Action {
        SNEAK_KEY,
        MODE_KEY,
        ALT_KEY,
        WHEEL_UP,
        WHEEL_DOWN,
        JUMP_KEY
    }

    public class PlayerAction {
        boolean[] stateMap = new boolean[Action.values().length];
    }
}
