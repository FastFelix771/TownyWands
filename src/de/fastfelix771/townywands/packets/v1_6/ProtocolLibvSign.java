package de.fastfelix771.townywands.packets.v1_6;

import java.util.HashMap;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.packets.VirtualSign;
import de.fastfelix771.townywands.utils.Invoker;
import de.fastfelix771.townywands.utils.ReturningInvoker;

@SuppressWarnings("deprecation")
public class ProtocolLibvSign implements VirtualSign {

    protected static final HashMap<String, Invoker<String[]>> pending = new HashMap<>();

    @Override @SneakyThrows
    public void show(Player player, Invoker<String[]> callback) {
        if(pending.containsKey(player.getName())) return;
        pending.put(player.getName(), callback);

        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(133);
        
        packet.getIntegers()
        .write(0, 0)
        .write(1, 0)
        .write(2, 0)
        .write(3, 0);

        TownyWands.getPacketHandler().sendPacket(player, packet);
    }

    @Override
    public void setup(final Player player) {

        TownyWands.getPacketHandler().addPacketListener(player, PacketType.Play.Client.UPDATE_SIGN, new ReturningInvoker<PacketContainer, Boolean>() {
            
            @Override
            public Boolean invoke(PacketContainer packet) {
                if(!pending.containsKey(player.getName())) return false;
                pending.remove(player.getName()).invoke(packet.getStringArrays().read(0));
                return true;
            }
        }, true);

    }

    @Override
    public void unsetup(Player player) {
        pending.remove(player.getName());
    }

}