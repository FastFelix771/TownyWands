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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import de.fastfelix771.townywands.main.TownyWands;

public class Utils {

	private static final Set<Integer> validCounts = Sets.newHashSet(9, 18, 27, 36, 45, 54); // faster 'contains' method, but it may break some backwards compatibility, needs testing.

	public static boolean isValidSlotCount(int slots) {
		return validCounts.contains(slots);
	}

	public static void bungeeConnect(Player player, String servername) {
		if (TownyWands.getConfiguration().bungee) {
			try(ByteArrayOutputStream bytes = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(bytes)) {
				out.writeUTF("Connect");
				out.writeUTF(servername);
				player.sendPluginMessage(TownyWands.getInstance(), "BungeeCord", bytes.toByteArray());
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

}
