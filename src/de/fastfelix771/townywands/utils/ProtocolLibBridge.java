package de.fastfelix771.townywands.utils;

import java.util.concurrent.Callable;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Synchronized;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.utils.Reflect.Version;

public final class ProtocolLibBridge {

    @SneakyThrows
    public static void spawnSign(@NonNull Player p) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_ENTITY);

        if (Reflect.getServerVersion() == Version.v1_8 || Reflect.getServerVersion() == Version.v1_9) {
            packet.getBlockPositionModifier().write(0, new BlockPosition(0, 0, 0));
         }

        if (Reflect.getServerVersion() == Version.v1_7) {
            packet.getIntegers().write(0, 0).write(1, 0).write(2, 0);
        }

        ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet);
    }

    @SneakyThrows
    public static void addHandler(@NonNull final Player p, @NonNull final vSign gui) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(TownyWands.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.UPDATE_SIGN) {

            @Override @Synchronized
            public void onPacketReceiving(PacketEvent e) {
                if (e.getPacketType() != PacketType.Play.Client.UPDATE_SIGN) return;
                if (gui.getPending().containsKey(p.getUniqueId())) {
                    final Invoker<String[]> invoker = gui.getPending().remove(p.getUniqueId());
                    final String[] lines = new String[4];

                    if (Reflect.getServerVersion() == Version.v1_8 || Reflect.getServerVersion() == Version.v1_9) {
                        WrappedChatComponent[] components = e.getPacket().getChatComponentArrays().read(0);
                        for (int i = 0; i < components.length; i++) {
                            WrappedChatComponent c = components[i];
                            lines[i] = c.getJson();
                        }
                    }

                    if (Reflect.getServerVersion() == Version.v1_7) {
                        String[] array = e.getPacket().getStringArrays().read(0);
                        for(int i = 0; i < array.length; i++) {
                            lines[i] = array[i];
                        }
                    }

                    //Prevents concurrency issues with Netty & Bukkit
                    Bukkit.getScheduler().callSyncMethod(TownyWands.getInstance(), new Callable<Void>() {

                        @Override
                        public Void call() throws Exception {
                            invoker.invoke(lines);
                            return null;
                        }});
                }
            }
        });
    }

}
