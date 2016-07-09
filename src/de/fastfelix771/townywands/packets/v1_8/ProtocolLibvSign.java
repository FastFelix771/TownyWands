package de.fastfelix771.townywands.packets.v1_8;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.packets.VirtualSign;
import de.fastfelix771.townywands.utils.Invoker;

public class ProtocolLibvSign extends de.fastfelix771.townywands.packets.v1_7.ProtocolLibvSign implements VirtualSign {

    @Override
    public void show(Player player, Invoker<String[]> callback) {
        if(pending.containsKey(player.getName())) return;
        pending.put(player.getName(), callback);

        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);

        packet.getBlockPositionModifier().write(0, new BlockPosition(0, 0, 0));

        TownyWands.getPacketHandler().sendPacket(player, packet);
    }
    
}