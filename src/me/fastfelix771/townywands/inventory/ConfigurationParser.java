package me.fastfelix771.townywands.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import me.fastfelix771.townywands.lang.Language;
import me.fastfelix771.townywands.main.Mainclass;
import me.fastfelix771.townywands.utils.DataBundle;
import me.fastfelix771.townywands.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ConfigurationParser {

	private final YamlConfiguration config;
	private final Level lvl;
	private final boolean async;
	private boolean error;

	public ConfigurationParser(final YamlConfiguration config, final Level loglevel, final boolean async) {
		if (config == null) {
			throw new IllegalArgumentException("config cannot be null");
		}
		if (loglevel == null) {
			throw new IllegalArgumentException("loglevel cannot be null");
		}
		this.config = config;
		this.lvl = loglevel;
		this.async = async;
		this.error = false;
	}

	public ConfigurationParser(final YamlConfiguration config, final boolean async) {
		if (config == null) {
			throw new IllegalArgumentException("config cannot be null");
		}
		this.config = config;
		this.lvl = Level.INFO;
		this.async = async;
		this.error = false;
	}

	public boolean parse() {
		final Runnable job = new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {

				final ConfigurationSection sec_inventories = config.getConfigurationSection("inventories");
				if (sec_inventories == null) {
					error("ConfigurationSection 'inventories' not found in file '" + config.getName() + "'");
					return;
				}

				final Map<String, Object> inventories = sec_inventories.getValues(false);

				for (final String str_name : inventories.keySet()) {
					final ConfigurationSection inv = sec_inventories.getConfigurationSection(str_name);

					final String name = inv.getString("name"); // Name of the inventory
					final int slots = inv.getInt("slots"); // Slotcount...obviously
					final String command = inv.getString("command");
					final String permission = inv.getString("permission"); // permission needed to open the inventory
					final ConfigurationSection items = inv.getConfigurationSection("items"); // contents of the inventory

					if (!Utils.isValidSlotCount(slots)) {
						error("Inventory '" + str_name + "' is wrong configurated! Field 'slots' has an invalid value.\n" + "It needs to be 9,18,27,36,45 or 54 due to limitations of minecraft.");
						return;
					}

					if (items == null) {
						error("Inventory '" + str_name + "' is wrong configurated! ConfigurationSection 'items' does not exist.");
						return;
					}

					if (command.replace(" ", "").equalsIgnoreCase("")) {
						error("Inventory '" + str_name + "' is wrong configurated! Field 'command' cannot be empty.");
						return;
					}

					final Map<String, Object> item_values = items.getValues(false);

					// Store some temporary data here.
					final HashMap<Language, DataBundle> dbs = new HashMap<Language, DataBundle>();

					for (final String item_name : item_values.keySet()) {
						final ConfigurationSection i = items.getConfigurationSection(item_name);

						if (i.get("itemID") == null || i.get("metaID") == null) {
							error("Item '" + item_name + "' is wrong configured! The field 'itemID' or 'metaID' does not exist!");
							continue;
						}

						final int id = i.getInt("itemID");
						final int metaid = i.getInt("metaID");
						final Material material = Material.getMaterial(id);

						// Skip to next item if the given material doesnt exist.
						if (material == null) {
							error("Item '" + item_name + "' is wrong configured! The field 'itemID' has an invalid value.");
							continue;
						}

						if (i.get("slot") == null) {
							error("Item '" + item_name + "' is wrong configured! The field 'slot' doesnt exist!");
							continue;
						}

						final int slot = i.getInt("slot") - 1;

						if (i.get("quantity") == null) {
							error("Item '" + item_name + "' is wrong configured! The field 'quantity' doesnt exist!");
							continue;
						}

						final int quantity = i.getInt("quantity");

						// Create the item itself from the given data and enhance it via the ItemBuilder.
						for (final Language lang : Language.values()) {

							final String langcode = lang.getCode();

							String iname = i.getString("name_" + langcode);
							final List<String> ilore = i.getStringList("lore_" + langcode);
							final List<String> icommands = i.getStringList("commands_" + langcode);

							// Skip to next language if one or more of the parameters are missing.
							if (iname == null || ilore == null || icommands == null) {
								continue;
							}

							// Colorize the name and lore
							iname = ChatColor.translateAlternateColorCodes('&', iname);
							for (int i2 = 0; i2 < ilore.size(); i2++) {
								ilore.set(i2, ChatColor.translateAlternateColorCodes('&', ilore.get(i2)));
							}

							DataBundle db = null;

							if (dbs.containsKey(lang)) {
								db = dbs.get(lang);
							} else {
								db = InventoryBuilder.build(ChatColor.translateAlternateColorCodes('&', name), command, permission, slots, lang);
								dbs.put(lang, db);
							}

							// Create the basic item itself and enhance it via the ItemBuilder
							final ItemStack iitem = new ItemStack(material, quantity, (short) metaid);
							ItemBuilder.build(db.getCommand(), db.getInventory(), iitem, slot, iname, ilore, icommands, lang);

						}
					}

					// Now save all DataBundles and clear the tempstorage.
					for (final Entry<Language, DataBundle> ent : dbs.entrySet()) {
						final DataBundle db = ent.getValue();
						db.save();
					}
					dbs.clear();

				}

			}

		};

		if (async) {
			new Thread(job).start();
		} else {
			job.run();
		}

		final boolean err = error;
		this.error = false; // Resetting error after parsing to make the parser re-usable.

		return err;
	}

	private void error(final String message) {
		this.error = true;
		Mainclass.getInstance().getLogger().log(lvl, message);
	}

}