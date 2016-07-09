package de.fastfelix771.townywands.packets.v1_7;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.packets.VirtualSign;
import de.fastfelix771.townywands.utils.Invoker;

public class ProtocolLibvSign extends de.fastfelix771.townywands.packets.v1_6.ProtocolLibvSign implements VirtualSign {
	
    @Override
    public void show(Player player, Invoker<String[]> callback) {
        if(pending.containsKey(player.getName())) return;
        pending.put(player.getName(), callback);

        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);

        packet.getIntegers()
        .write(0, 0)
        .write(1, 0)
        .write(2, 0);

        TownyWands.getPacketHandler().sendPacket(player, packet);
    }

}