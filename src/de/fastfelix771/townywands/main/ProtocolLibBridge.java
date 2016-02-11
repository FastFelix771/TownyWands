package de.fastfelix771.townywands.main;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import de.fastfelix771.townywands.utils.Invoker;
import de.fastfelix771.townywands.utils.Reflect;
import de.fastfelix771.townywands.utils.Reflect.Version;
import de.fastfelix771.townywands.utils.SignGUI;

// Experimental, this class will probably get enhanced soon.
// Currently its ONLY build to work with SignGUI's.
// Soon, i'll rewrite the whole packet-related part of TownyWands.. or with other words: vSigns v2.0! :D
public final class ProtocolLibBridge {

    @SneakyThrows
    public static void spawnSign(@NonNull Player p) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_ENTITY);

        if (Reflect.getServerVersion() == Version.v1_8) {
            packet.getBlockPositionModifier().write(0, new BlockPosition(0, 0, 0));
        }

        if (Reflect.getServerVersion() == Version.v1_7) {
            packet.getIntegers().write(0, 0).write(1, 0).write(2, 0);
        }

        ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet);
    }

    @SneakyThrows
    public static void addHandler(@NonNull final Player p, @NonNull final SignGUI gui) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(TownyWands.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.UPDATE_SIGN) {

            @Override
            public void onPacketReceiving(PacketEvent e) {
                if (e.getPacketType() != PacketType.Play.Client.UPDATE_SIGN) return;
                if (gui.inputResults.containsKey(p.getUniqueId())) {
                    Invoker<String[]> invoker = gui.inputResults.remove(p.getUniqueId());
                    String[] lines = new String[4];

                    if (Reflect.getServerVersion() == Version.v1_8) {
                        WrappedChatComponent[] components = e.getPacket().getChatComponentArrays().read(0);
                        for (int i = 0; i < components.length; i++) {
                            WrappedChatComponent c = components[i];
                            lines[i] = c.getJson();
                        }
                    }

                    if (Reflect.getServerVersion() == Version.v1_7) {
                        lines = e.getPacket().getStringArrays().read(0);
                    }

                    invoker.invoke(lines);
                }
            }
        });
    }

}
