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
		if (TownyWands.isBungeecord()) {
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