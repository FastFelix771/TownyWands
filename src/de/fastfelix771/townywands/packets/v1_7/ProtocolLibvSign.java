package de.fastfelix771.townywands.packets.v1_7;

import java.util.HashMap;
import java.util.UUID;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.packets.VirtualSign;
import de.fastfelix771.townywands.utils.Invoker;
import de.fastfelix771.townywands.utils.Reflect;
import de.fastfelix771.townywands.utils.ReturningInvoker;

public class ProtocolLibvSign implements VirtualSign {

    private static final HashMap<UUID, Invoker<String[]>> pending = new HashMap<>();

    @Override @SneakyThrows
    public void show(Player player, Invoker<String[]> callback) {
        if(pending.containsKey(player.getUniqueId())) return;
        pending.put(player.getUniqueId(), callback);

        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_ENTITY);

        packet.getIntegers()
        .write(0, 0)
        .write(1, 0)
        .write(2, 0);

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    @Override
    public void setup(final Player player) {

        TownyWands.getPacketHandler().addPacketListener(player, Reflect.PacketPlayInUpdateSign, new ReturningInvoker<Object, Boolean>() {
            
            @Override
            public Boolean invoke(Object packet) {
                if(!pending.containsKey(player.getUniqueId())) return false;
                pending.remove(player.getUniqueId()).invoke(((PacketContainer) packet).getStringArrays().read(0));
                return true;
            }
        }, true);

    }

    @Override
    public void unsetup(Player player) {
        pending.remove(player.getUniqueId());
    }

}