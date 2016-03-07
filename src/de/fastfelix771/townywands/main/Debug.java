package de.fastfelix771.townywands.main;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.Callable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.java.Log;

@Log
public final class Debug {

    public static final HashSet<UUID> players = new HashSet<>();
    public static boolean console = false;

    public static void msg(@NonNull final String... strings) {
        Bukkit.getScheduler().callSyncMethod(TownyWands.getInstance(), new Callable<Void>() {

            @Override @Synchronized
            public Void call() throws Exception {
                if (console) for (String string : strings)
                    log.warning(string);
                for (UUID uuid : players) {
                    if (Bukkit.getPlayer(uuid) == null) players.remove(uuid);
                    else {
                        Player p = Bukkit.getPlayer(uuid);
                        for (String string : strings)
                            p.sendMessage(string);
                    }
                }
                return null;
            }

        });
    }

}