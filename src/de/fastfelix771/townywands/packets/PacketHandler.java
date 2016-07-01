package de.fastfelix771.townywands.packets;

import org.bukkit.entity.Player;

import de.fastfelix771.townywands.utils.ReturningInvoker;

public abstract interface PacketHandler {
    
    public void sendPacket(Player player, Object packet);
    public void addPacketListener(Player player, Object packetRelatedObject, ReturningInvoker<Object, Boolean> invoker, boolean dropPacketOnError);
    
}