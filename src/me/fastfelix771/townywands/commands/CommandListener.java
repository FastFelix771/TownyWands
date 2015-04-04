package me.fastfelix771.townywands.commands;

import me.fastfelix771.townywands.main.InventoryHandler;
import me.fastfelix771.townywands.main.Mainclass;
import me.fastfelix771.townywands.main.PlayerHandler;
import me.fastfelix771.townywands.utils.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CommandListener implements CommandExecutor {

	// It should be very obvious what this method does...
	@Override
	public final boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("twa")) {
				if (p.hasPermission("townywands.gui.admin")) {
					InventoryHandler.admin.show(p);
				} else {
					Util.sendPermError(p);
				}
			} else if (cmd.getName().equalsIgnoreCase("twu")) {
				if (p.hasPermission("townywands.gui.user")) {
					PlayerHandler.getCorrectGUI(p).show(p);
				} else {
					Util.sendPermError(p);
				}
			}
		} else {
			Mainclass.console.sendMessage("§4This command is only for players!");
		}
		return true;
	}
}