package me.fastfelix771.townywands.inventory;

import java.util.List;

import me.fastfelix771.townywands.utils.Utils;
import me.fastfelix771.townywands.utils.Utils.Type;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

	public static void build(final String key, final Inventory inventory, ItemStack item, final int slot, final String displayname, final List<String> lore, final List<String> commands, final List<String> console_commands, final boolean enchanted) {

		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayname);
		meta.setLore(lore);
		item.setItemMeta(meta);

		item = Utils.setCommands(item, commands, Type.PLAYER);
		item = Utils.setCommands(item, console_commands, Type.CONSOLE);
		item = Utils.setKey(item, key);

		if (enchanted) {
			item = Utils.addEnchantmentGlow(item);
		}

		item = Utils.hideFlags(item);

		inventory.setItem(slot, item);

	}

}