package de.fastfelix771.townywands.netty;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.Bukkit;
import de.fastfelix771.townywands.main.Debug;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.utils.ReturningInvoker;

@RequiredArgsConstructor
public final class MessageToMessageDecoder_1_7 extends MessageToMessageDecoder<Object> {

    @NonNull private final ReturningInvoker<Object, Boolean> invoker;
    @NonNull private final Class<?> packetClass;
    private final boolean dropPacketOnError;

    @Override
    protected void decode(ChannelHandlerContext chc, final Object packet, List<Object> packetList) {
        try {
            if (packetClass.isInstance(packet)) {
                boolean dropPacket = false;
                Future<Boolean> future = Bukkit.getScheduler().callSyncMethod(TownyWands.getInstance(), new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {
                        boolean bool = invoker.invoke(packet);
                        return bool;
                    }});

                while(!future.isDone()) {}
                dropPacket = future.get();
                Debug.msg("§aSuccessfully called vSign handlers!");
                if (dropPacket) return;
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
            Debug.msg("§cThere was an error while calling vSign handlers!", "§cA detailed error has been sent to the console.");
            if (dropPacketOnError) return;
        }
        packetList.add(packet);
    }

}