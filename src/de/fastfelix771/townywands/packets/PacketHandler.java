package de.fastfelix771.townywands.packets;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import de.fastfelix771.townywands.utils.ReturningInvoker;

public abstract interface PacketHandler {
    
    public void sendPacket(Player player, PacketContainer packet);
    public void addPacketListener(Player player, PacketType type, ReturningInvoker<PacketContainer, Boolean> invoker, boolean dropPacketOnError);
    
}