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
package de.fastfelix771.townywands.files;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.JSONArray;

import de.fastfelix771.townywands.api.inventories.InventoryCommand;
import de.fastfelix771.townywands.api.inventories.ModularInventory;
import de.fastfelix771.townywands.api.inventories.ModularItem;
import de.fastfelix771.townywands.lang.Language;
import de.fastfelix771.townywands.utils.Debug;
import de.fastfelix771.townywands.utils.Documents;
import de.fastfelix771.townywands.utils.Utils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor 
@SuppressWarnings("all")
public class HybridParser {

	@NonNull
	@Getter @Setter
	private YamlConfiguration config;

	private final File file;


	private void parseInventory(ConfigurationSection inv) {
		String command = inv.getString("command");
		String permission = inv.getString("permission");
		String title = inv.getString("name");
		int slots = inv.getInt("slots");

		if (title.length() > 32) {
			Debug.log(title + " has an invalid title! Maximum allow length is 32 characters!");
			return;
		}

		if (!Utils.isValidSlotCount(slots)) {
			Debug.log(title + " has an invalid slot count! It can only be either 9, 18, 27, 36, 45 or 54!");
			return;
		}
		
		// replace color codes
		title = title.replace('&', '§');

		ModularInventory inventory = new ModularInventory();

		inventory.setCommand(command);
		inventory.setPermission(permission);
		inventory.setTitle(title);
		inventory.setSize(slots);

		ConfigurationSection items = inv.getConfigurationSection("items");
		Map<String, Object> values = items.getValues(false);

		for(String itemName : values.keySet()) {
			parseItem(items.getConfigurationSection(itemName), inventory);
		}

		try {
			Documents.saveDefault("inventories", ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', title)).trim(), inventory);
			file.renameTo(Paths.get(file.getAbsolutePath().concat(".converted")).toFile());
		} catch (JAXBException e) {
			Debug.log("Failed to convert old inventories to XML!");
			Debug.log("Exception: " + e.toString());
		}
	}

	private void parseItem(ConfigurationSection item, ModularInventory inventory) {
		Material material = Material.getMaterial(item.getInt("itemID"));
		short metaID = (short) item.getInt("metaID");
		int slot = item.getInt("slot") - 1;
		int quantity = item.getInt("quantity");
		boolean enchanted = item.getBoolean("enchanted");

		if(material == null) return;

		JSONArray commands = new JSONArray();
		JSONArray consoleCommands = new JSONArray();

		if(item.getStringList("commands") != null) commands.addAll(item.getStringList("commands"));
		if(item.getStringList("console_commands") != null) consoleCommands.addAll(item.getStringList("console_commands"));

		for (Language language : Language.values()) {
			if (language != Language.ENGLISH) continue;

			String displayName = item.getString("name_" + language.getCode());
			List<String> loreList = item.getStringList("lore_" + language.getCode());

			if(displayName == null || loreList == null) {
				Debug.log(inventory.getTitle() + "'s Item at slot " + slot + " is missing a DisplayName or Lore!");	
				continue;
			}

			if (!(slot >= 0 && slot < 54)) {
				Debug.log(inventory.getTitle() + "'s Item at slot " + slot + " has an invalid slot number! It must be between 0 and 53 (including these)!");
				continue;
			}
			
			// Translate color codes
			displayName = displayName.replace('&', '§');
			
			for (int i = 0; i < loreList.size(); i++) {
				loreList.set(i, loreList.get(i).replace('&', '§'));
			}

			ModularItem modularItem = new ModularItem();

			modularItem.setAmount(quantity);
			modularItem.setDisplayName(displayName);
			modularItem.setEnchanted(enchanted);
			modularItem.setHideFlags(true);
			modularItem.setMaterial(material);
			modularItem.setSlot(slot);
			modularItem.setMetaID(metaID);
			modularItem.getLore().addAll(loreList);

			if (!commands.isEmpty()) {
				for (Object command : commands) {
					String cmd = (String) command;
					modularItem.getCommands().add(new InventoryCommand(cmd, false));
				}
			}

			if (!consoleCommands.isEmpty()) {
				for (Object command : consoleCommands) {
					String cmd = (String) command;
					modularItem.getCommands().add(new InventoryCommand(cmd, true));
				}
			}

			inventory.getItems().add(modularItem);
		}
	}

	public void parse() {
		ConfigurationSection inventories = this.config.getConfigurationSection("inventories");

		for (String invName : inventories.getValues(false).keySet()) {
			long start = System.currentTimeMillis();
			Bukkit.getConsoleSender().sendMessage("§cTownyWands | §bConverting inventory §3" + invName + " §binto the new XML format!");

			ConfigurationSection inv = inventories.getConfigurationSection(invName);
			parseInventory(inv);

			long end = System.currentTimeMillis();
			Bukkit.getConsoleSender().sendMessage("§cTownyWands | §bConversion of inventory §3" + invName + " §btook §3" + (end - start) + "§bms");
		}

	}

}
