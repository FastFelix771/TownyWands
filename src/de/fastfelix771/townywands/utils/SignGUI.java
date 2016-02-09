package de.fastfelix771.townywands.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Synchronized;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import de.fastfelix771.townywands.netty.NettyUtils;
import de.fastfelix771.townywands.utils.Reflect.Version;

/**
 * @author Janhektor This class in licensed under GPLv3 For more information look at http://www.gnu.org/licenses/gpl-3.0
 * @modifiedBy FastFelix771 for Java 7 and better integration into TownyWands
 */
public final class SignGUI implements Listener, Runnable {

    private final HashMap<UUID, Invoker<String[]>> inputResults = new HashMap<>();

    public SignGUI(@NonNull JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0L, 20 * 3L);
    }

    public boolean open(@NonNull Player p, @NonNull Invoker<String[]> result) {
        this.inputResults.put(p.getUniqueId(), result);
        try {
            final Class<?> packetClass = Reflect.getNMSClass("PacketPlayOutOpenSignEditor");
            Object packet = null;

            if (Reflect.getServerVersion() == Version.v1_8) {
                final Class<?> blockPositionClass = Reflect.getNMSClass("BlockPosition");
                final Constructor<?> packetCon = Reflect.getConstructor(packetClass, blockPositionClass);
                packet = packetCon.newInstance(Reflect.getConstructor(blockPositionClass, int.class, int.class, int.class).newInstance(0, 0, 0));
            }

            if (Reflect.getServerVersion() == Version.v1_7) {
                final Constructor<?> packetCon = Reflect.getConstructor(packetClass, int.class, int.class, int.class);
                packet = packetCon.newInstance(0, 0, 0);
            }

            if (packet == null) return false;
            Reflect.sendPacket(p, packet);
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /* Garbage Collection */
    @Override
    @Synchronized
    public void run() {
        for (final UUID uuid : this.inputResults.keySet())
            if (Bukkit.getPlayer(uuid) == null) this.inputResults.remove(uuid);
    }

    /* Events */
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        NettyUtils.addHandler("decoder", "TownyWands_vSigns", p, Reflect.getNMSClass("PacketPlayInUpdateSign"), new Invoker<Object>() {

            @Override
            @SneakyThrows
            public void invoke(Object packet) {
                String[] lines = new String[4];

                if (Reflect.getServerVersion() == Version.v1_8) {
                    final Object chatBaseComponents = Reflect.getMethod(packet.getClass().getMethod("b")).invoke(packet);
                    for (int i = 0; i < 4; i++) {
                        final Object chatComponent = Array.get(chatBaseComponents, i);
                        final Method getText = chatComponent.getClass().getMethod("getText");
                        lines[i] = (String) getText.invoke(chatComponent);
                    }
                }

                if (Reflect.getServerVersion() == Version.v1_7) {
                    lines = (String[]) Reflect.getMethod(packet.getClass().getMethod("f")).invoke(packet);
                }

                if (SignGUI.this.inputResults.containsKey(p.getUniqueId())) {
                    SignGUI.this.inputResults.remove(p.getUniqueId()).invoke(lines);
                }
            }
        }, true); // dropping the packet will prevent errors with other plugins.
    }

}