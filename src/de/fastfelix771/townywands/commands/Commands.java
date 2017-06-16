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
package de.fastfelix771.townywands.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.fastfelix771.townywands.api.inventories.Inventories;
import de.fastfelix771.townywands.commands.CommandController.CommandHandler;
import de.fastfelix771.townywands.commands.CommandController.SubCommandHandler;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.utils.Debug;
import de.fastfelix771.townywands.utils.Utils;
import de.unitygaming.bukkit.vsign.util.Invoker;

public class Commands {

	private static final List<String> commands = Arrays.asList("§c/vsign §a<player> [some command using {data}]", "§c/townywands §ahelp", "§c/townywands §a?", "§c/townywands §alist", "§c/townywands §adebug");

	@CommandHandler(
			name = "townywands",
			description = "Provides basic features.",
			usage = "/townywands ?",
			permission = "townywands.cmd.townywands",
			permissionMessage = "§cYou are missing the permission §atownywands.cmd.townywands§c!",
			aliases = { "tws" })
	public void townywands(CommandSender sender, String[] args) {
		sender.sendMessage("§6=====================================================");
		sender.sendMessage("§bTowny§3Wands §6- §av§c" + TownyWands.getInstance().getDescription().getVersion());
		sender.sendMessage("§2Created by §6FastFelix771");
		sender.sendMessage("§cIf you need help, use §a/townywands help");
		sender.sendMessage("§6=====================================================");
	}

	@SubCommandHandler(
			name = "help",
			parent = "townywands",
			permission = "townywands.cmd.help",
			permissionMessage = "§cYou are missing the permission §atownywands.cmd.help§c!")
	public void townywands_help(CommandSender sender, String[] args) {
		sender.sendMessage("§6=====================================================");
		sender.sendMessage("§bTowny§3Wands §6- §aCommands");

		for (int i = 0; i < commands.size(); i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(commands.get(i));
			sender.sendMessage(sb.toString());
		}

		sender.sendMessage("§6=====================================================");
	}

	@SubCommandHandler(
			name = "?",
			parent = "townywands",
			permission = "townywands.cmd.help",
			permissionMessage = "§cYou are missing the permission §atownywands.cmd.help§c!")
	public void townywands_help2(CommandSender sender, String[] args) {
		this.townywands_help(sender, args);
	}

	@SubCommandHandler(
			name = "debug",
			parent = "townywands",
			permission = "townywands.cmd.debug",
			permissionMessage = "§cYou are missing the permission §atownywands.cmd.debug§c!")
	public void townywands_debug(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (Debug.players.contains(((Player) sender).getName())) Debug.players.remove(((Player) sender).getName());
			else Debug.players.add(((Player) sender).getName());
			sender.sendMessage(String.format("§6TownyWands DebugMode: §4%s", String.valueOf(Debug.players.contains(((Player) sender).getName())).toLowerCase()));
			return;
		}
		Debug.console = Debug.console ? false : true;
		sender.sendMessage(String.format("§6TownyWands DebugMode: §4%s", String.valueOf(Debug.console).toLowerCase()));

	}
	
	@SubCommandHandler(
			name = "list",
			parent = "townywands",
			permission = "townywands.cmd.list",
			permissionMessage = "§cYou are missing the permission §atownywands.cmd.list§c!")
	public void townywands_list(CommandSender sender, String[] args) {
		sender.sendMessage("Not yet implemented.");
		sender.sendMessage("§cdev-Edition: §6" + Inventories.dump());
	}

	@CommandHandler(
			name = "vsign",
			description = "Creates a virtual Sign and executes commands from the given input.",
			usage = "/vsign playername command {data}",
			permission = "townywands.cmd.vsign",
			permissionMessage = "§cYou are missing the permission §atownywands.cmd.vsign§c!")
	public void vsign(CommandSender sender, final String[] args) {
		if ((args.length == 0) || (Bukkit.getPlayerExact(args[0]) == null)) return;

		final StringBuilder sb = new StringBuilder();
		for (int i = 1; i < args.length; i++) {
			if(!args[i].trim().isEmpty()) sb.append(args[i]).append(" ");
		}

		TownyWands.getSignAPI().open(Bukkit.getPlayerExact(args[0]), new Invoker<String[]>() {

			@Override
			public void invoke(String[] lines) {
				StringBuilder data = new StringBuilder();
				for (String line : lines)
					if (line != null && !line.trim().isEmpty()) data.append(line);

				String command = sb.toString();
				if(command.trim().isEmpty()) return;

				command = command.substring(0, command.length() - 1);
				command = command.replace("{data}", data.toString());

				Bukkit.dispatchCommand(Bukkit.getPlayerExact(args[0]), command); // Maybe add console commands here too?
			}
		});
	}

	@CommandHandler(
			name = "bungeetp",
			description = "Teleports a player to another server in your bungeecord network.",
			usage = "/bungeetp playername servername",
			permission = "townywands.cmd.bungeetp",
			permissionMessage = "§cYou are missing the permission §atownywands.cmd.bungeetp§c!")
	public void bungeetp(CommandSender sender, String[] args) {
		if (!(args.length == 2)) return;

		if (Bukkit.getPlayerExact(args[0]) == null) return;

		Player player = Bukkit.getPlayerExact(args[0]);
		String servername = args[1];

		try {
			Utils.bungeeConnect(player, servername);
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendMessage("§cFailed to teleport §a" + player.getName() + "§c to server §a" + servername);
			return;
		}

	}

	@CommandHandler(
			name = "fakecmd",
			description = "Teleports a player to another server in your bungeecord network.",
			usage = "/fakecmd playername commandToExecute",
			permission = "townywands.cmd.fakecmd",
			permissionMessage = "§cYou are missing the permission §atownywands.cmd.fakecmd§c!")
	public void fakecmd(CommandSender sender, String[] args) {
		if ((args.length < 2) || (Bukkit.getPlayerExact(args[0]) == null)) return;

		StringBuilder sb = new StringBuilder();

		for (int i = 1; i < args.length; i++) {
			sb.append(args[i]).append(" ");
		}

		Bukkit.getPlayerExact(args[0]).chat("/".concat(sb.toString()));

	}

	// MODIFY //

	@CommandHandler(
			name = "modify",
			permission = "townywands.cmd.modify",
			permissionMessage = "§cYou are missing the permission §atownywands.cmd.modify§c!")
	public void modify(Player sender, String[] args) {

	}

	@SubCommandHandler(parent = "modify", name= "help")
	public void modify_help(Player sender, String[] args) {

	}

	@SubCommandHandler(parent = "modify", name= "?")
	public void modify_questionmark(Player sender, String[] args) {
		modify_help(sender, args);
	}


	// GUI //

	@CommandHandler(
			name = "gui",
			permission = "townywands.cmd.gui",
			permissionMessage = "§cYou are missing the permission §atownywands.cmd.gui§c!")
	public void gui(Player sender, String[] args) {

	}

	@SubCommandHandler(parent = "gui", name= "help")
	public void gui_help(Player sender, String[] args) {

	}

	@SubCommandHandler(parent = "gui", name= "?")
	public void gui_questionmark(Player sender, String[] args) {
		gui_help(sender, args);
	}

	@SubCommandHandler(parent = "gui", name= "list")
	public void gui_list(Player sender, String[] args) {
		sender.sendMessage("§6=====================================================");

		//		for(ModularGUI gui : ModularGUI.loadAll()) {
		//			sender.sendMessage(String.format("§6<§3%s§6> - §a%d §6Storages - §a/%s §6- §c%s", gui.getName(), gui.getInventories().size(), gui.getCommand(), gui.getPermission()));
		//		}

		sender.sendMessage("§6=====================================================");
	}


	// INV //

	@CommandHandler(
			name = "inv",
			permission = "townywands.cmd.inv",
			permissionMessage = "§cYou are missing the permission §atownywands.cmd.inv§c!")
	public void inv(Player sender, String[] args) {
		sender.sendMessage("§6=====================================================");

		//		for(ModularInventory inv : ModularInventory.loadAll()) {
		//			sender.sendMessage(String.format("§6<§3%d§6> - §6GUI: §a%s§6 - §a%d §6Slots - §a%d §6Items", inv.getID(), inv.getGUI().getName(), inv.getSlots(), ModularItem.loadAll(inv).size()));
		//		}

		sender.sendMessage("§6=====================================================");
	}

	@SubCommandHandler(parent = "inv", name= "help")
	public void inv_help(Player sender, String[] args) {

	}

	@SubCommandHandler(parent = "inv", name= "?")
	public void inv_questionmark(Player sender, String[] args) {
		inv_help(sender, args);
	}

}
