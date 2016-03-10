package de.fastfelix771.townywands.packets.v1_8;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import de.fastfelix771.townywands.main.Debug;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.packets.PacketHandler;
import de.fastfelix771.townywands.packets.PacketHandler.NettySupport;
import de.fastfelix771.townywands.utils.ReturningInvoker;

public class NMSHandler implements PacketHandler, NettySupport {

    @Override
    public void sendPacket(Player player, Object packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet<?>) packet);
    }

    @Override
    public void addPacketListener(Player player, final Object packetClass, final ReturningInvoker<Object, Boolean> invoker, final boolean dropPacketOnError) {
        ((Channel) getChannel(player)).pipeline().addAfter("decoder", "TownyWands_vSigns_".concat(UUID.randomUUID().toString()), new MessageToMessageDecoder<Packet<?>>() {

            @Override
            protected void decode(ChannelHandlerContext chc, final Packet<?> packet, List<Object> forward) {

                if(!((Class<?>) packetClass).isInstance(packet)) {
                    forward.add(packet);
                    return;
                } // If the packet's class isnt the one we are looking for, forward the packet to the server and do nothing.
                
                try {
                    
                    Future<Boolean> future = Bukkit.getScheduler().callSyncMethod(TownyWands.getInstance(), new Callable<Boolean>() {

                        @Override
                        public Boolean call() throws Exception {
                          return invoker.invoke(packet);
                        }});

                    while(!future.isDone()) {}
                    if (future.get()) return;
                    
                } catch(Throwable t) {
                    t.printStackTrace();
                    Debug.msg("§cThere was an error while calling vSign handlers!", "§cA detailed error has been sent to the console.");
                    if (dropPacketOnError) return;
                }
                
                forward.add(packet);
                
            }
            
        });
        
    }

    @Override
    public Object getChannel(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
    }

}