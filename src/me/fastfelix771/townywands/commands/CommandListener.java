package me.fastfelix771.townywands.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public final class CommandListener implements CommandExecutor, Listener {

	@Override
	public final boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2, final String[] arg3) {

		return true;
	}

	//This checks if someone enter the /t gui or /towny gui command without blocking functions of towny by registring the command in the plugin.yml
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public final void onCmd(final PlayerCommandPreprocessEvent e) {
		final Player p = e.getPlayer();
		final String cmd = e.getMessage().split(" ")[0].replace("/", "").toLowerCase();
		final String[]args = cmd.split(" ");
		if(cmd.equalsIgnoreCase("t") | cmd.equalsIgnoreCase("towny")) {
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("gui")) {
					e.setCancelled(true); //This prevents towny from throwing errors like "invalid subcommand"
					
				}
			}
		}
	}
}
