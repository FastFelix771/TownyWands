package de.fastfelix771.townywands.packets;

import org.bukkit.entity.Player;
import de.fastfelix771.townywands.utils.ReturningInvoker;

/**
 * Universal interface to handle packet sending & handling of incoming client packets.
 * @author FastFelix771
 */
public abstract interface PacketHandler {
    
    public void sendPacket(Player player, Object packet);
    public void addPacketListener(Player player, Object packetRelatedObject, ReturningInvoker<Object, Boolean> invoker, boolean dropPacketOnError);

    public static abstract interface NettySupport {

        public Object getChannel(Player player);
        
    }
    
}