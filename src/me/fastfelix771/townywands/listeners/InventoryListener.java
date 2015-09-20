package me.fastfelix771.townywands.listeners;

import java.util.List;

import me.fastfelix771.townywands.lang.Language;
import me.fastfelix771.townywands.utils.DataBundle;
import me.fastfelix771.townywands.utils.Database;
import me.fastfelix771.townywands.utils.Utils;

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

		// If the given command isnt connected to an inventory, do nothing.
		if (!Database.contains(command)) {
			return;
		}
		e.setCancelled(true);

		final Language lang = Language.getLanguage(p);
		DataBundle db = null;

		if (Database.containsData(command, lang)) {
			db = Database.get(command, lang);
		} else {
			if (!Database.containsData(command, Language.ENGLISH)) {
				p.sendMessage("§cTownyWans | §aThere is no GUI registered in your language nor the default one (ENGLISH), please report this to an administrator!");
				return;
			}
			db = Database.get(command, Language.ENGLISH);
		}

		final String permission = db.getPermission();
		final Inventory inv = db.getInventory();

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
		final Language language = Language.getLanguage(p);
		final Inventory inv = e.getClickedInventory();

		if (inv == p.getInventory()) {
			return;
		}

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

		List<String> commands = null;

		if (Utils.getCommands(item, language) != null) {
			commands = Utils.getCommands(item, language);
		} else if (Utils.getCommands(item, Language.ENGLISH) != null) {
			commands = Utils.getCommands(item, Language.ENGLISH);
		} else {
			Bukkit.getConsoleSender().sendMessage("§cTownyWands | Could not find any commands for item §r" + item.getItemMeta().getDisplayName());
			return;
		}

		for (final String cmd : commands) {
			Bukkit.dispatchCommand(p, cmd);
		}

	}

}