package me.fastfelix771.townywands.inventory;

import me.fastfelix771.townywands.lang.Language;
import me.fastfelix771.townywands.utils.DataBundle;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class InventoryBuilder {

	public static DataBundle build(final String title, final String command, final String permission, final int slots, final Language language) {

		final Inventory inv = Bukkit.createInventory(null, slots, title);

		final DataBundle db = new DataBundle(command, permission, inv, language);

		return db;

	}

}