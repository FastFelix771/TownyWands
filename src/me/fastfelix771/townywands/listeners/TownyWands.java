package me.fastfelix771.townywands.listeners;

import java.util.Arrays;
import java.util.List;

import me.fastfelix771.townywands.main.Mainclass;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TownyWands implements CommandExecutor {

	private static final List<String> commands = Arrays.asList("help", "?", "reload");

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (!sender.hasPermission("townywands.cmd.townywands")) {
			sender.sendMessage("§cYou are missing the permission '§atownywands.cmd.townywands§c'");
			return false;
		}

		if (args.length > 1) {
			sender.sendMessage("§cInvalid arguments! Type §a/townywands help §cfor help.");
		}

		if (args.length == 0) {
			sender.sendMessage("§6======================================");
			sender.sendMessage("§bTowny§3Wands §6- §av§c" + Mainclass.getInstance().getDescription().getVersion());
			sender.sendMessage("§2Created by §6FastFelix771");
			sender.sendMessage("§6======================================");
		}

		if (args.length == 1) {
			switch (args[0]) {
			case "reload":
				if (!sender.hasPermission("townywands.cmd.reload")) {
					sender.sendMessage("§cYou are missing the permission '§atownywands.cmd.reload§c'");
				}
				Mainclass.reload();
				sender.sendMessage("§bTowny§3Wands §ahas been reloaded!");
				break;
			case "help":
				if (!sender.hasPermission("townywands.cmd.help")) {
					sender.sendMessage("§cYou are missing the permission '§atownywands.cmd.help§c'");
				}
				sender.sendMessage("§6======================================");
				sender.sendMessage("§bTowny§3Wands §6- §aCommands");
				sender.sendMessage("§2/townywands");
				commands.forEach(command -> sender.sendMessage("§2/townywands §b" + command));
				sender.sendMessage("§6======================================");
				break;
			case "?":
				if (!sender.hasPermission("townywands.cmd.help")) {
					sender.sendMessage("§cYou are missing the permission '§atownywands.cmd.help§c'");
				}
				sender.sendMessage("§6======================================");
				sender.sendMessage("§bTowny§3Wands §6- §aCommands");
				sender.sendMessage("§2/townywands");
				commands.forEach(command -> sender.sendMessage("§2/townywands §b" + command));
				sender.sendMessage("§6======================================");
				break;
			default:
				sender.sendMessage("§cInvalid arguments! Type §a/townywands help §cfor help.");
				break;
			}

		}

		return true;
	}

}
