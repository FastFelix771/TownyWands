package me.fastfelix771.townywands.utils;

import java.io.File;

import me.fastfelix771.townywands.main.Mainclass;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.spigotmc.ProtocolInjector.PacketTitle.Action;

public final class Util {

	private static Util instance;

	private final File msgfile = new File(Mainclass.getInstance().getDataFolder().getAbsolutePath() + "/messages.yml");
	private final YamlConfiguration messages = YamlConfiguration.loadConfiguration(msgfile);

	/**
	 * @author FastFelix771
	 * @param title
	 *            sets the title you want to send
	 * @param subtitle
	 *            sets the subtitle you want to send
	 * @param staytime
	 *            How much seconds the player should see the titles?
	 * @param fadein
	 *            how much seconds should it take to fade in the title?
	 * @param fadeout
	 *            how much seconds should it take to fade out the title?
	 * @param player
	 *            is just the player who sees the title when its done :3
	 */
	public static void sendTitle(final String title, final String subtitle, final int staytime, final int fadein, final int fadeout, final Player player) {
		final Title tit = new Title(colorize(title), Action.TITLE, fadein, staytime, fadeout);
		final Title sub = new Title(colorize(subtitle), Action.SUBTITLE, fadein, staytime, fadeout);
		tit.build();
		sub.build();
		tit.send(player);
		sub.send(player);
	}

	/**
	 * @author FastFelix771
	 * @param message
	 * @return The input message but now with colors! Great for config messages!
	 */
	public static final String colorize(final String message) {
		final String colo = message.replaceAll("&([0-9a-fA-Fk-pK-PrR])", "§$1");
		return colo;
	}

	/**
	 * @author FastFelix771
	 * @param message
	 * @param target
	 * @param replacement
	 * @return the input string with your wished replacements, good for config messages too!
	 */
	public static final String replace(final String message, final String target, final String replacement) {
		final String rplcd = message.replace(target, replacement);
		return rplcd;
	}

	/**
	 * @author FastFelix771
	 * @param player
	 *            receives the No Permission error from the config!
	 */
	public static final void sendPermError(final Player player) {
		player.sendMessage(colorize(getInstance().messages.getString("noPermissions")));
	}

	// Dirty trick to get my messagefile ._.
	private static final Util getInstance() {
		return instance;
	}
}