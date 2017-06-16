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
package de.fastfelix771.townywands.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import de.fastfelix771.townywands.main.TownyWands;
import lombok.NonNull;
import lombok.extern.java.Log;

@Log
public final class Debug {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final HashSet<String> players = new HashSet<>();
	public static boolean console = false;

	public static synchronized void log(@NonNull final String... strings) {
		Bukkit.getScheduler().callSyncMethod(TownyWands.getInstance(), new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				File logFile = Paths.get("plugins", "TownyWands", "logs", String.format("debug-%s.log", DATE_FORMAT.format(new Date()))).toFile();
				if (!logFile.exists()) logFile.getParentFile().mkdirs();
				
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
					for (String string : strings) {
						writer.write(ChatColor.stripColor(string).concat("\n"));
					}
				}

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
