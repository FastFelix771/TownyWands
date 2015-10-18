package me.fastfelix771.townywands.listeners;

import java.util.ArrayList;
import java.util.List;

import me.fastfelix771.townywands.inventory.ModularGUI;
import me.fastfelix771.townywands.lang.Language;
import me.fastfelix771.townywands.utils.Database;
import me.fastfelix771.townywands.utils.Utils;
import me.fastfelix771.townywands.utils.Utils.Type;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

	@EventHandler
	public void onCommand(final PlayerCommandPreprocessEvent e) {
		final String command = e.getMessage().substring(1, e.getMessage().length());
		final Player p = e.getPlayer();

		if (!Database.contains(command)) {
			return;
		}
		e.setCancelled(true);

		final Language lang = Language.getLanguage(p);
		final ModularGUI gui = Database.get(command);
		Inventory inv = null;

		if (gui.contains(lang)) {
			inv = gui.get(lang);
		} else {
			if (!gui.contains(Language.ENGLISH)) {
				p.sendMessage("§cTownyWands | §aThere is no GUI registered in your language nor the default one (§6ENGLISH§a)!");
				p.sendMessage("§cPlease report this to an administrator!");
				return;
			}
			inv = gui.get(Language.ENGLISH);
		}

		final String permission = gui.getPermission();
		if (!p.hasPermission(permission)) {
			p.sendMessage("§cYou are missing the permission '§a" + permission + "§c'.");
			return;
		}

		p.openInventory(inv);

	}

	@EventHandler
	public void onInvClick(final InventoryClickEvent e) {
		final Player p = (Player) e.getWhoClicked();
		final ItemStack item = e.getCurrentItem();

		if (item == null || item.getType().equals(Material.AIR)) {
			return;
		}

		if (Utils.getKey(item) == null) {
			return;
		}
		e.setCancelled(true);

		final String command = Utils.getKey(item);

		if (!Database.contains(command)) {
			return;
		}

		List<String> commands = new ArrayList<String>();
		List<String> console_commands = new ArrayList<String>();

		if (Utils.getCommands(item, Type.PLAYER) != null) {
			commands = Utils.getCommands(item, Type.PLAYER);
		}

		if (Utils.getCommands(item, Type.CONSOLE) != null) {
			console_commands = Utils.getCommands(item, Type.CONSOLE);
		}

		for (String cmd : commands) {
			if (cmd.replace(" ", "").isEmpty()) {
				continue;
			}

			cmd = cmd.replace("{playername}", p.getName());
			cmd = cmd.replace("{uuid}", p.getUniqueId().toString());
			cmd = cmd.replace("{health}", String.valueOf(p.getHealth()));
			cmd = cmd.replace("{world}", p.getWorld().getName());
			cmd = cmd.replace("{hunger}", String.valueOf(p.getFoodLevel()));
			cmd = cmd.replace("{saturation}", String.valueOf(p.getSaturation()));
			cmd = cmd.replace("{displayname}", p.getDisplayName());

			Bukkit.dispatchCommand(p, cmd);
		}

		for (String cmd : console_commands) {
			if (cmd.replace(" ", "").isEmpty()) {
				continue;
			}

			cmd = cmd.replace("{playername}", p.getName());
			cmd = cmd.replace("{uuid}", p.getUniqueId().toString());
			cmd = cmd.replace("{health}", String.valueOf(p.getHealth()));
			cmd = cmd.replace("{world}", p.getWorld().getName());
			cmd = cmd.replace("{hunger}", String.valueOf(p.getFoodLevel()));
			cmd = cmd.replace("{saturation}", String.valueOf(p.getSaturation()));
			cmd = cmd.replace("{displayname}", p.getDisplayName());

			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}

	}

}