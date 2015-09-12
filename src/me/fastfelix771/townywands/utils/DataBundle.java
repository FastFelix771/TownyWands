package me.fastfelix771.townywands.utils;

import me.fastfelix771.townywands.lang.Language;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

// This is an probably temporary class to hold all the data needed to create the inventories...my brain will explode if i continue with spreading that data everywhere in my code!!!
public class DataBundle {

	private final String command;
	private final String permission;
	private final Inventory inventory;
	private final Language language;
	private boolean saved = false;

	public DataBundle(final String command, final String permission, final Inventory inventory, final Language language) {
		this.command = command;
		this.permission = permission;
		this.inventory = inventory;
		this.language = language;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public String getPermission() {
		return permission;
	}

	public Language getLanguage() {
		return language;
	}

	public String getCommand() {
		return command;
	}

	public void save() {
		if (!saved) {
			this.saved = true;
			if (!Database.containsData(getCommand(), getLanguage())) {
				Database.add(getCommand(), this);
				Bukkit.getConsoleSender().sendMessage("§cInventory §r" + getInventory().getTitle() + " §6(§a" + getLanguage().getName() + "§6) §csaved!");
			}
		}
	}

}