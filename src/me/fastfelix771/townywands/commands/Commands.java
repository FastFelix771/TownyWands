package me.fastfelix771.townywands.commands;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import me.fastfelix771.townywands.commands.CommandController.CommandHandler;
import me.fastfelix771.townywands.commands.CommandController.SubCommandHandler;
import me.fastfelix771.townywands.lang.Language;
import me.fastfelix771.townywands.main.Mainclass;
import me.fastfelix771.townywands.utils.Database;
import me.fastfelix771.townywands.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands {

	private static final List<String> commands = Arrays.asList("help", "?", "reload");

	@CommandHandler(name = "townywands", description = "Provides basic features.", usage = "/townywands ?", permission = "townywands.cmd.townywands", permissionMessage = "§cYou are missing the permission §atownywands.cmd.townywands§c!", aliases = { "tws" })
	public void townywands(final CommandSender sender, final String[] args) {
		sender.sendMessage("§6======================================");
		sender.sendMessage("§bTowny§3Wands §6- §av§c" + Mainclass.getInstance().getDescription().getVersion());
		sender.sendMessage("§2Created by §6FastFelix771");
		sender.sendMessage("§6======================================");
	}

	@SubCommandHandler(name = "reload", parent = "townywands", permission = "townywands.cmd.reload", permissionMessage = "§cYou are missing the permission §atownywands.cmd.reload§c!")
	public void townywands_reload(final CommandSender sender, final String[] args) {
		Mainclass.reload();
		sender.sendMessage("§bTowny§3Wands §ahas been reloaded!");
	}

	@SubCommandHandler(name = "help", parent = "townywands", permission = "townywands.cmd.help", permissionMessage = "§cYou are missing the permission §atownywands.cmd.help§c!")
	public void townywands_help(final CommandSender sender, final String[] args) {
		sender.sendMessage("§6======================================");
		sender.sendMessage("§bTowny§3Wands §6- §aCommands");
		sender.sendMessage("§2/townywands");
		commands.forEach(command -> sender.sendMessage("§2/townywands §b" + command));
		sender.sendMessage("§6======================================");
	}

	@SubCommandHandler(name = "?", parent = "townywands", permission = "townywands.cmd.help", permissionMessage = "§cYou are missing the permission §atownywands.cmd.help§c!")
	public void townywands_help2(final CommandSender sender, final String[] args) {
		sender.sendMessage("§6======================================");
		sender.sendMessage("§bTowny§3Wands §6- §aCommands");
		sender.sendMessage("§2/townywands");
		commands.forEach(command -> sender.sendMessage("§2/townywands §b" + command));
		sender.sendMessage("§6======================================");
	}

	@CommandHandler(name = "vsign", description = "Creates a virtual Sign and executes commands from the given input.", usage = "/vsign playername command {data}", permission = "townywands.cmd.vsign", permissionMessage = "§cYou are missing the permission §atownywands.cmd.vsign§c!")
	public void vsign(final CommandSender sender, final String[] args) {
		if (args.length == 0 || Bukkit.getPlayerExact(args[0]) == null) {
			return;
		}

		// If not 1.8, do nothing.
		if (Mainclass.getSignGUI() == null) {
			return;
		}

		final Player player = Bukkit.getPlayerExact(args[0]);
		final StringBuffer buffer = new StringBuffer();

		for (int i = 1; i < args.length; i++) {
			final String arg = args[i];
			buffer.append(arg + " ");
		}

		Mainclass.getSignGUI().open(player, new Consumer<String[]>() {

			@Override
			public void accept(final String[] lines) {
				final StringBuffer data = new StringBuffer();
				for (final String line : lines) {
					if (!line.isEmpty()) {
						data.append(line);
					}
				}

				String command = buffer.toString();
				command = command.substring(0, command.length() - 1);
				command = command.replace("{data}", data.toString());

				Bukkit.dispatchCommand(player, command); // Maybe add console commands here too?
			}
		});
	}

	@CommandHandler(name = "bungeetp", description = "Teleports a player to another server in your bungeecord network.", usage = "/bungeetp playername servername", permission = "townywands.cmd.bungeetp", permissionMessage = "§cYou are missing the permission §atownywands.cmd.bungeetp§c!")
	public void bungeetp(final CommandSender sender, final String[] args) {
		if (!(args.length == 2)) {
			return;
		}

		if (Bukkit.getPlayerExact(args[0]) == null) {
			return;
		}

		final Player player = Bukkit.getPlayerExact(args[0]);
		final String servername = args[1];

		try {
			Utils.bungeeConnect(player, servername); //If bungeecord is disabled the method will do nothing.
		} catch (final Exception e) {
			e.printStackTrace();
			sender.sendMessage("Failed to teleport " + player.getName() + " to server " + servername);
			return;
		}

	}

	@CommandHandler(name = "gui", description = "Creation and changing of GUIs", usage = "/gui help", permission = "townywands.cmd.gui", permissionMessage = "§cYou are missing the permission §atownywands.cmd.gui§c!")
	public void gui(final CommandSender sender, final String[] args) {
		sender.sendMessage("Patience my Padawan, this will feature will be added soon!"); // :o funny, isn't it? No? Hm :(
	}

	@SubCommandHandler(name = "list", parent = "gui", permission = "townywands.cmd.gui.list", permissionMessage = "§cYou are missing the permission §atownywands.cmd.gui.list§c!")
	public void gui_list(final CommandSender sender, final String[] args) {
		sender.sendMessage("§6======================================");
		sender.sendMessage("§bTowny§3Wands §6- §aGUI's");
		Mainclass.getParser().getInventoryTokens().forEach(token -> sender.sendMessage("§b§l" + token + " §3§l- §r" + Database.get(Mainclass.getParser().getConfig().getConfigurationSection("inventories").getConfigurationSection(token).getString("command"), Language.ENGLISH).getInventory().getTitle()));
		sender.sendMessage("§6======================================");
	}

}