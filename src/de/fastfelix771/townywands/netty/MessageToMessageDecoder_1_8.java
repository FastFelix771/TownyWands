package de.fastfelix771.townywands.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import de.fastfelix771.townywands.utils.Invoker;

@RequiredArgsConstructor
public final class MessageToMessageDecoder_1_8 extends MessageToMessageDecoder<Object> implements ChannelHandler {

    @NonNull
    private final Invoker<Object> invoker;
    @NonNull
    private final Class<?> packetClass;
    private final boolean dropPacket;

    @Override
    protected void decode(ChannelHandlerContext chc, Object packet, List<Object> packetList) throws Exception {
        if (packetClass.isInstance(packet)) {
            invoker.invoke(packet);
            if (dropPacket) return;
        }
        packetList.add(packet);
    }

}