package me.fastfelix771.townywands.utils;

import org.bukkit.entity.Player;
import org.spigotmc.ProtocolInjector.PacketTitle.Action;

public final class Util {

	public static void sendTitle(final String title, final String subtitle, final int staytime, final int fadein, final int fadeout, final Player player) {
		final Title tit = new Title(colorize(title), Action.TITLE, fadein, staytime, fadeout);
		final Title sub = new Title(colorize(subtitle), Action.SUBTITLE, fadein, staytime, fadeout);
		tit.build();
		sub.build();
		tit.send(player);
		sub.send(player);
	}

	public static final String colorize(final String message) {
		final String colo = message.replaceAll("&([0-9a-fA-Fk-pK-PrR])", "§$1");
		return colo;
	}

	public static final String replace(final String message, final String target, final String replacement) {
		final String rplcd = message.replace(target, replacement);
		return rplcd;
	}
}