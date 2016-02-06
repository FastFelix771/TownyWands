package me.fastfelix771.townywands.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

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
            final Class<?> blockPositionClass = Reflect.getNMSClass("BlockPosition");
            final Constructor<?> blockPosCon = blockPositionClass.getConstructor(int.class, int.class, int.class);
            final Object blockPosition = blockPosCon.newInstance(0, 0, 0);
            final Constructor<?> packetCon = packetClass.getConstructor(blockPositionClass);
            final Object packet = packetCon.newInstance(blockPosition);
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
        NettyUtils.getPipeline(p).addAfter("decoder", "TownyWands_vSigns", new MessageToMessageDecoder<Object>() {
            @Override
            protected void decode(ChannelHandlerContext chc, Object packet, List<Object> packetList) throws Exception {
                if (Reflect.getNMSClass("PacketPlayInUpdateSign").isInstance(packet)) {
                    final Object chatBaseComponents = Reflect.getMethod(packet.getClass().getMethod("b")).invoke(packet);
                    final String[] lines = new String[4];
                    for (int i = 0; i < 4; i++) {
                        final Object chatComponent = Array.get(chatBaseComponents, i);
                        final Method getText = chatComponent.getClass().getMethod("getText");
                        lines[i] = (String) getText.invoke(chatComponent);
                    }
                    if (SignGUI.this.inputResults.containsKey(p.getUniqueId())) {
                        SignGUI.this.inputResults.remove(p.getUniqueId()).invoke(lines);
                        return; // This will prevent the packet from being sent to the EventSystem, to avoid errors with other plugins.
                    }
                }
                packetList.add(packet);
            }
        });
    }

}