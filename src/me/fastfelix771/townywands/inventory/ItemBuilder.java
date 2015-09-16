package me.fastfelix771.townywands.inventory;

import java.util.List;

import me.fastfelix771.townywands.lang.Language;
import me.fastfelix771.townywands.utils.Utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

	public static void build(final String key, final Inventory inventory, ItemStack item, final int slot, final String displayname, final List<String> lore, final List<String> commands, final Language language, final boolean enchant) {

		final ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.values());
		meta.setDisplayName(displayname);
		meta.setLore(lore);
		item.setItemMeta(meta);

		item = Utils.setCommands(item, commands, language);
		item = Utils.setKey(item, key);

		if (enchant) {
			Utils.addEnchantmentGlow(item);
		}

		inventory.setItem(slot, item);

	}

}