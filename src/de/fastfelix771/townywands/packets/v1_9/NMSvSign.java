package de.fastfelix771.townywands.packets.v1_9;

import java.util.HashMap;
import java.util.UUID;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.PacketPlayInUpdateSign;
import net.minecraft.server.v1_9_R1.PacketPlayOutOpenSignEditor;
import org.bukkit.entity.Player;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.packets.VirtualSign;
import de.fastfelix771.townywands.utils.Invoker;
import de.fastfelix771.townywands.utils.ReturningInvoker;

public class NMSvSign implements VirtualSign {

    private static final HashMap<UUID, Invoker<String[]>> pending = new HashMap<>();

    @Override
    public void show(Player player, Invoker<String[]> callback) {
        if(pending.containsKey(player.getUniqueId())) return;
        pending.put(player.getUniqueId(), callback);

        TownyWands.getPacketHandler().sendPacket(player, new PacketPlayOutOpenSignEditor(new BlockPosition(0, 0, 0)));
    }

    @Override
    public void setup(final Player player) {
        TownyWands.getPacketHandler().addPacketListener(player, PacketPlayInUpdateSign.class, new ReturningInvoker<Object, Boolean>() {

            @Override
            public Boolean invoke(Object packet) {
                if(!pending.containsKey(player.getUniqueId())) return false;
                pending.remove(player.getUniqueId()).invoke(((PacketPlayInUpdateSign) packet).b());
                return true;
            }
        }, true);

    }

    @Override
    public void unsetup(Player player) {
        pending.remove(player.getUniqueId());
    }

}