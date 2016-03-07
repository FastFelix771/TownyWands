package de.fastfelix771.townywands.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Synchronized;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import de.fastfelix771.townywands.main.Debug;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.netty.NettyUtils;
import de.fastfelix771.townywands.utils.Reflect.Version;

public final class vSign implements Listener {

    @NonNull @Getter(value=AccessLevel.PACKAGE) private Map<UUID, Invoker<String[]>> pending = new HashMap<>();
    @Getter(value=AccessLevel.PACKAGE) private String uniqueHandlerName = "TownyWands_vSigns_".concat(UUID.randomUUID().toString());

    public vSign(@NonNull Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public vSign(@NonNull Plugin plugin, @NonNull Map<UUID, Invoker<String[]>> map) {
        this.pending = map;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void show(@NonNull Player player, @NonNull Invoker<String[]> callback) {
        UUID uuid = player.getUniqueId();

        if(getPending().containsKey(uuid)) {
            Debug.msg("§cTried to show a second vSign while another one is already showing!", String.format("§aRelated player: §a%s", player.getName()));
            return;
        }

        getPending().put(uuid, callback);

        if (TownyWands.isProtocolLibEnabled()) {
            try {
                ProtocolLibBridge.spawnSign(player);
                return;
            }
            catch (Exception e) {
                e.printStackTrace();
                Debug.msg("§cError while opening a vSign via ProtocolLib!", "§aA detailed error has been sent to the console!");
            }
            return;
        }

        try {
            final Class<?> packetClass = Reflect.getNMSClass("PacketPlayOutOpenSignEditor");
            Object packet = null;

            if (Reflect.getServerVersion() == Version.v1_8  || Reflect.getServerVersion() == Version.v1_9) {
                final Class<?> blockPositionClass = Reflect.getNMSClass("BlockPosition");
                packet = Reflect.getConstructor(packetClass, blockPositionClass).newInstance(Reflect.getConstructor(blockPositionClass, int.class, int.class, int.class).newInstance(0, 0, 0));
            }

            if (Reflect.getServerVersion() == Version.v1_7) {
                final Constructor<?> packetCon = Reflect.getConstructor(packetClass, int.class, int.class, int.class);
                packet = packetCon.newInstance(0, 0, 0);
            }

            if (packet == null) return;
            Reflect.sendPacket(player, packet);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Debug.msg("§cError while opening a vSign via TownyWands' internal methods!", "§aA detailed error has been sent to the console!");
        }

    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();

        if (TownyWands.isProtocolLibEnabled()) {
            ProtocolLibBridge.addHandler(p, this);
            return;
        }

        NettyUtils.addHandler("decoder", uniqueHandlerName, p, Reflect.getNMSClass("PacketPlayInUpdateSign"), new ReturningInvoker<Object, Boolean>() {

            @Override
            @SneakyThrows
            @Synchronized
            public Boolean invoke(Object packet) {

                if (getPending().containsKey(p.getUniqueId())) {
                    Invoker<String[]> invoker = getPending().remove(p.getUniqueId());
                    String[] lines = new String[4];
                    
                    if(Reflect.getServerVersion() == Version.v1_9) {
                        lines = (String[]) Reflect.getMethod(packet.getClass().getMethod("b")).invoke(packet);
                    }

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

                    invoker.invoke(lines);
                    return true;
                }

                return false;
            }
        }, true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        getPending().remove(e.getPlayer().getUniqueId());
    }

}