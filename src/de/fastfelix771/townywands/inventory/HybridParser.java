package de.fastfelix771.townywands.inventory;

import java.nio.charset.StandardCharsets;
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
import de.fastfelix771.townywands.utils.Base64;
import de.fastfelix771.townywands.utils.Compressor;
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

			if(!commands.isEmpty()) entity.setCommands(Base64.getInstance().print(Compressor.getInstance().compress(commands.toJSONString().getBytes(StandardCharsets.UTF_8))));
			if(!consoleCommands.isEmpty()) entity.setConsoleCommands(Base64.getInstance().print(Compressor.getInstance().compress(consoleCommands.toJSONString().getBytes(StandardCharsets.UTF_8))));
			if(loreList != null && !itemLore.isEmpty()) entity.setLore(Base64.getInstance().print(Compressor.getInstance().compress(itemLore.toJSONString().getBytes(StandardCharsets.UTF_8))));

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