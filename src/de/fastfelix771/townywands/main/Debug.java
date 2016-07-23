package de.fastfelix771.townywands.main;

import java.util.HashSet;
import java.util.concurrent.Callable;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;

@Log // TODO: Use this class MUCH more to give "/tws debug" more power.
public final class Debug {

    public static final HashSet<String> players = new HashSet<>();
    public static boolean console = false;

    public static void msg(@NonNull final String... strings) {
        Bukkit.getScheduler().callSyncMethod(TownyWands.getInstance(), new Callable<Void>() {

            @Override @Synchronized
            public Void call() throws Exception {
                if (console) for (String string : strings)
                    log.warning(string);
                for (String name : players) {
                    if (Bukkit.getPlayer(name) == null) players.remove(name);
                    else {
                        for (String string : strings)
                            Bukkit.getPlayer(name).sendMessage(string);
                    }
                }
                return null;
            }

        });
    }

}