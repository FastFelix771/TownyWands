package de.fastfelix771.townywands.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import de.fastfelix771.townywands.main.Debug;
import de.fastfelix771.townywands.utils.ReturningInvoker;

@RequiredArgsConstructor
public final class MessageToMessageDecoder_1_8 extends MessageToMessageDecoder<Object> implements ChannelHandler {

    @NonNull
    private final ReturningInvoker<Object, Boolean> invoker;
    @NonNull
    private final Class<?> packetClass;
    private final boolean dropPacketOnError;

    @Override
    protected void decode(ChannelHandlerContext chc, Object packet, List<Object> packetList) throws Exception {
        try { // This hopefully prevents players from being kicked.
            if (packetClass.isInstance(packet)) {
                boolean dropPacket = invoker.invoke(packet);
                if (dropPacket) return;
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
            Debug.msg("§cThere was an error while handling a vSign!", "§cA detailed error has been sent to the console.");
            if (dropPacketOnError) return;
        }
        packetList.add(packet);
    }

}