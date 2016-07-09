package de.fastfelix771.townywands.packets.v1_6;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.packets.PacketHandler;
import de.fastfelix771.townywands.utils.ReturningInvoker;
import lombok.SneakyThrows;

public class ProtocolLibHandler implements PacketHandler {

    @Override @SneakyThrows(InvocationTargetException.class)
    public void sendPacket(Player player, PacketContainer packet) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    @Override
    public void addPacketListener(final Player player, final PacketType type, final ReturningInvoker<PacketContainer, Boolean> invoker, final boolean dropPacketOnError) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(TownyWands.getInstance(), type) {

            @Override
            public void onPacketReceiving(PacketEvent e) {
                if(!(e.getPlayer().getName().equalsIgnoreCase(player.getName()))) return;
                if(!(e.getPacketType() == type)) return;

                try{
                    e.setCancelled(invoker.invoke(e.getPacket()));
                } catch(Throwable t) {
                    t.printStackTrace();
                    e.setCancelled(dropPacketOnError);
                }

            }

        });
    }

}