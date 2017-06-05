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
package de.fastfelix771.townywands.inventory;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.JSONArray;

import de.fastfelix771.townywands.dao.EntityGUI;
import de.fastfelix771.townywands.dao.EntityInventory;
import de.fastfelix771.townywands.dao.EntityItem;
import de.fastfelix771.townywands.lang.Language;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.utils.Utils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor @SuppressWarnings("all")
public class HybridParser {

	@NonNull
	@Getter @Setter
	private YamlConfiguration config;

	private void parseGUI(ConfigurationSection gui) {
		String command = gui.getString("command");
		String permission = gui.getString("permission");

		EntityGUI entity = new EntityGUI();

		entity.setName(gui.getName());
		entity.setCommand(command);
		entity.setPermission(permission);

		TownyWands.getInstance().getDatabase().save(entity);

		parseInventory(gui);
	}

	private void parseInventory(ConfigurationSection inv) {
		String title = inv.getString("name");
		int slots = inv.getInt("slots");

		if (title.length() > 32 || !Utils.isValidSlotCount(slots)) {
			return;
		}

		EntityInventory entity = new EntityInventory();

		entity.setGui(inv.getName());
		entity.setTitle(title);
		entity.setSlots(slots);
		entity.setEnabled(true); // Theres only 1 Inventory available with the old config system - no sense in disabling the single inventory.

		TownyWands.getInstance().getDatabase().save(entity);

		ConfigurationSection items = inv.getConfigurationSection("items");
		Map<String, Object> values = items.getValues(false);

		for(String itemName : values.keySet()) {
			parseItem(items.getConfigurationSection(itemName), inv.getName());
		}

	}

	private void parseItem(ConfigurationSection item, String guiName) {
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

		EntityInventory inventory = TownyWands.getInstance().getDatabase().find(EntityInventory.class).where().eq("gui", guiName).findUnique();
		
		for (Language language : Language.values()) {			
			String displayName = item.getString("name_" + language.getCode());
			List<String> loreList = item.getStringList("lore_" + language.getCode());
			
			if(displayName == null || loreList == null || loreList.isEmpty()) continue;

			JSONArray itemLore = new JSONArray();
			itemLore.addAll(loreList);

			EntityItem entity = new EntityItem();

			entity.setLanguage(language);
			entity.setAmount(quantity);
			if(displayName != null) entity.setDisplayname(displayName);
			entity.setEnchanted(enchanted);
			entity.setHideFlags(true);
			entity.setMaterial(material);
			entity.setSlot(slot);
			entity.setMetaID(metaID);

			if(!commands.isEmpty()) entity.setCommands(commands.toJSONString());
			if(!consoleCommands.isEmpty()) entity.setConsoleCommands(consoleCommands.toJSONString());
			if(loreList != null && !itemLore.isEmpty()) entity.setLore(itemLore.toJSONString());

			entity.setInventory(inventory.getId());
			TownyWands.getInstance().getDatabase().save(entity);
		}
	}

	public void parse() {
		ConfigurationSection inventories = this.config.getConfigurationSection("inventories");

		for (String guiName : inventories.getValues(false).keySet()) {
			long start = System.currentTimeMillis();
			Bukkit.getConsoleSender().sendMessage("§cTownyWands | §bSetup of inventory §3" + guiName + " §bhas started");

			ConfigurationSection gui = inventories.getConfigurationSection(guiName);
			parseGUI(gui);

			long end = System.currentTimeMillis();
			Bukkit.getConsoleSender().sendMessage("§cTownyWands | §bMigration of inventory §3" + guiName + " §btook §3" + (end - start) + "§bms");
		}

	}

}
