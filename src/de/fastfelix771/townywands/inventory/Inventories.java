package de.fastfelix771.townywands.inventory;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.fastfelix771.townywands.api.events.GuiOpenEvent;
import de.fastfelix771.townywands.files.ModularInventory;
import de.fastfelix771.townywands.files.ModularItem;
import de.fastfelix771.townywands.main.Debug;
import de.fastfelix771.townywands.utils.Documents;

/**
 * Manages inventories internally. <br>
 * Responsible for firing related API events, loading GUIs from disk and setting them up for ingame use.
 */
public final class Inventories {

	private static final Map<String, ModularInventory> inventories = Collections.synchronizedMap(new HashMap<String, ModularInventory>());

	/**
	 * Checks if an inventory is attached to the given command.
	 */
	public static boolean exist(String command) {
		return inventories.containsKey(command);
	}

	/**
	 * Simply hands out the inventory attached to the command, or <code>null</code> if there is none.
	 */
	public static ModularInventory get(String command) {
		return inventories.get(command);
	}

	/**
	 * Converts the given {@link ModularInventory} into a Bukkit {@link Inventory} and displays it to the given user. <br>
	 * This method is only responsible for converting and displaying the inventory to the user, permission checks, click events and such has to be handled elsewhere! <br>
	 * To all items, an NBT code will be attached to allow EventListeners to distinguish "Click-Items" from usual ones. <br>
	 * There will also be other information about the underlying GUI attached to every item.
	 */
	public static void display(ModularInventory inventory, Player player) {
		if (inventory == null || player == null || !player.isOnline()) return;

		Inventory inv = Bukkit.createInventory(null, inventory.getSize(), inventory.getTitle());

		for (ModularItem item : inventory.getItems()) {
			ItemWrapper wrapper = ItemWrapper.wrap(new ItemStack(item.getMaterial(), item.getAmount(), item.getMetaID()));

			wrapper.setDisplayName(item.getDisplayName());
			wrapper.setEnchanted(item.isEnchanted());
			wrapper.hideFlags(item.isHideFlags());
			wrapper.setLore(item.getLore());

			wrapper.setNBTKey("townywands.properties.marker", 1);
			wrapper.setNBTKey("townywands.properties.command", inventory.getCommand());
			wrapper.setNBTKey("townywands.properties.slot", item.getSlot());

			inv.setItem(item.getSlot(), wrapper.getItem());
		}

		GuiOpenEvent event = new GuiOpenEvent(player, inventory);
		Bukkit.getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			player.openInventory(inv);
		}
	}

	/**
	 * Loads all inventories from the <b>/plugins/TownyWands/inventories/</b> directory. <br>
	 * It validates the files before loading to make sure they are not damaged in any way. <br>
	 * If an Inventory doesn't get loaded, the debug log should give some informations about that.
	 */
	public static void loadAll() {
		File directory = Paths.get("plugins", "TownyWands", "inventories").toFile();

		directory.mkdirs();
		if (!directory.isDirectory()) return;

		File[] files = directory.listFiles();

		for (File file : files) {
			if (!file.isFile()) continue;
			if (!file.getName().endsWith(".xml")) continue;

			if (!Documents.validate("inventories", file.getName().substring(0, file.getName().length() - 4), ModularInventory.class)) continue;
			try {
				ModularInventory inventory = Documents.load("inventories", file.getName().substring(0, file.getName().length() - 4), ModularInventory.class);
				inventories.put(inventory.getCommand(), inventory);
			} catch (JAXBException e) {
				Debug.log("Could not load inventory from file ".concat(file.getAbsolutePath()).concat("!"));
				Debug.log("Exception caught while trying to load it: " + e.toString());
			}
		}
	}

	/**
	 * Dumps a list of commands bound to Inventories, for debug purposes. <br>
	 * This contains absolutely no sensitive data and is safe to share.
	 */
	public static String dump() {
		return Arrays.toString(inventories.keySet().toArray());
	}

}
