package me.fastfelix771.townywands.commands;

import java.util.Arrays;
import java.util.List;

import me.fastfelix771.townywands.commands.CommandController.CommandHandler;
import me.fastfelix771.townywands.commands.CommandController.SubCommandHandler;
import me.fastfelix771.townywands.main.Mainclass;

import org.bukkit.command.CommandSender;

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

	/*
	 * Not done yet.
	 * @CommandHandler(name = "gui", description = "Creation and changing of GUIs", usage = "/gui help", permission = "townywands.cmd.gui", permissionMessage = "§cYou are missing the permission §atownywands.cmd.gui§c!", aliases = { "townygui" })
	 * public void gui(final CommandSender sender, final String[] args) {
	 * }
	 * @SubCommandHandler(name = "list", parent = "gui", permission = "townywands.cmd.gui.list", permissionMessage = "§cYou are missing the permission §atownywands.cmd.gui.list§c!")
	 * public void gui_list(final CommandSender sender, final String[] args) {
	 * sender.sendMessage("§6======================================");
	 * sender.sendMessage("§bTowny§3Wands §6- §aGUI's"); // COnfigurationParser add getInventoryTokens.
	 * Database.storage.entrySet().forEach(entry -> sender.sendMessage("§b§l" + entry.getValue().get(Language.ENGLISH).getInventory().getTitle() + " §3§l- §r" + ""));
	 * sender.sendMessage("§6======================================");
	 * }
	 */

}