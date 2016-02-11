package de.fastfelix771.townywands.main;

import java.util.HashSet;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import lombok.NonNull;
import lombok.extern.java.Log;

@Log
public final class Debug {

    public static final HashSet<UUID> players = new HashSet<>();
    public static boolean console = false;

    public static void msg(@NonNull String... strings) {
        if (console) for (String string : strings)
            log.severe(string);
        for (UUID uuid : players) {
            if (Bukkit.getPlayer(uuid) == null) players.remove(uuid);
            else {
                Player p = Bukkit.getPlayer(uuid);
                for (String string : strings)
                    p.sendMessage(string);
            }
        }
    }

}