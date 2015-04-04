package me.fastfelix771.townywands.utils;

import net.minecraft.server.v1_7_R4.ChatComponentText;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spigotmc.ProtocolInjector.PacketTitle;
import org.spigotmc.ProtocolInjector.PacketTitle.Action;

public final class Title {
	String message = "";
	Action action = null;
	int in = 0;
	int out = 0;
	int stay = 0;

	public Title(final String m, final Action a, final int i, final int o, final int s) {
		message = m;
		action = a;
		in = i;
		out = o;
		stay = s;
	}

	public static final int TPV = 18;
	PacketTitle packet = null;

	public void build() {
		if (action == PacketTitle.Action.TIMES) {
			final PacketTitle title = new PacketTitle(action, in, stay, out);
			packet = title;
		} else if (action == PacketTitle.Action.CLEAR || action == PacketTitle.Action.RESET) {
			final PacketTitle title = new PacketTitle(action);
			packet = title;
		} else if (action == PacketTitle.Action.TITLE || action == PacketTitle.Action.SUBTITLE) {
			final PacketTitle title = new PacketTitle(action, new ChatComponentText(ChatColor.translateAlternateColorCodes('&', message)));
			packet = title;
		}
	}

	@SuppressWarnings("deprecation")
	public void send(final Player player) {
		if (packet == null)
			return;
		if (player == null) {
			for (final Player p : Bukkit.getOnlinePlayers()) {
				send(p);
			}
		} else if (is18(player)) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static boolean is18(final CommandSender sender) {
		return sender instanceof CraftPlayer && ((CraftPlayer) sender).getHandle().playerConnection.networkManager.getVersion() >= TPV;
	}
}