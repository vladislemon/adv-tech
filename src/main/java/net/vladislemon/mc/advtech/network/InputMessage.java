package net.vladislemon.mc.advtech.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.vladislemon.mc.advtech.AdvancedTechnology;
import net.vladislemon.mc.advtech.handler.InputHandler;

import java.util.UUID;

/**
 * Created by slimon on 16-05-17.
 */
public class InputMessage implements IMessage {

    UUID playerId;
    InputHandler.Action action;
    boolean state;

    public InputMessage() {}

    public InputMessage(UUID playerId, InputHandler.Action action, boolean state) {
        this.playerId = playerId;
        this.action = action;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerId = new UUID(buf.readLong(), buf.readLong());
        action = InputHandler.Action.values()[buf.readByte()];
        state = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(playerId.getMostSignificantBits());
        buf.writeLong(playerId.getLeastSignificantBits());
        buf.writeByte(action.ordinal());
        buf.writeBoolean(state);
    }

    public static class Handler implements IMessageHandler<InputMessage, IMessage> {

        @Override
        public IMessage onMessage(InputMessage message, MessageContext ctx) {
            if(ctx.side.isServer()) {
                AdvancedTechnology.inputHandler.onServerInput(message.playerId, message.action, message.state);
            }
            return null;
        }
    }
}
