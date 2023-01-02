package net.vladislemon.mc.advtech.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.play.client.C01PacketChatMessage;

/**
 * Created by Notezway on 04.01.2016.
 */
public class TestMessage implements IMessage {

    private int data;

    public TestMessage() {}

    public TestMessage(int data) {
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        data = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(data);
    }

    public int getData() {
        return data;
    }

    public static class Handler implements IMessageHandler<TestMessage, IMessage> {

        @Override
        public IMessage onMessage(TestMessage message, MessageContext ctx) {
            ctx.getServerHandler().processChatMessage(new C01PacketChatMessage(String.format("Received %s from %s", Integer.toString(message.data), ctx.getServerHandler().playerEntity.getDisplayName())));
            return null;
        }
    }
}