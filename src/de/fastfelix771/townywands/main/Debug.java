/*******************************************************************************
 * Copyright (C) 2017 Felix Drescher-Hackel
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.fastfelix771.townywands.main;

import java.util.HashSet;
import java.util.concurrent.Callable;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;

@Log
public final class Debug {

    public static final HashSet<String> players = new HashSet<>();
    public static boolean console = false;

    public static void log(@NonNull final String... strings) {
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
