package me.fastfelix771.townywands.inventory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.fastfelix771.townywands.lang.Language;
import me.fastfelix771.townywands.main.Mainclass;
import me.fastfelix771.townywands.utils.Database;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;

public class ModularGUI implements Cloneable {

	public final String internalName;
	private String command;
	private String permission;
	private final ConcurrentHashMap<Language, Inventory> guis;

	public ModularGUI(final String internalName, final String command, final String permission) {
		Validate.noNullElements(new Object[] { internalName, command, permission }, "ModularGUI values cannot be null!");

		this.guis = new ConcurrentHashMap<Language, Inventory>();
		this.internalName = internalName;
		this.command = command;
		this.permission = permission;
	}

	public static ModularGUI fromName(final String internalName) {
		for (final ModularGUI gui : Database.guiList()) {
			if (gui.internalName.equalsIgnoreCase(internalName)) {
				return gui;
			}
		}
		return null;
	}

	public void add(final Language language, final Inventory inventory) {
		this.guis.putIfAbsent(language, inventory);
	}

	public void addAll(final Map<Language, Inventory> inventories) {
		this.guis.putAll(inventories);
	}

	public void remove(final Language... languages) {
		for (int i = 0; i < languages.length; i++) {
			final Language language = languages[i];
			if (this.contains(language)) {
				this.guis.remove(language);
			}
		}
	}

	public boolean contains(final Language language) {
		return (this.get(language) != null);
	}

	public Inventory get(final Language language) {
		return (this.guis.containsKey(language) ? this.guis.get(language) : null);
	}

	public void setCommand(final String command) {
		this.command = command;
	}

	public void setPermission(final String permission) {
		this.permission = permission;
	}

	public String getCommand() {
		return this.command;
	}

	public String getPermission() {
		return this.permission;
	}

	public ConfigurationSection getSection() {
		return Mainclass.getParser().getConfig().getConfigurationSection("inventories").getConfigurationSection(this.internalName);
	}

	@Override
	public ModularGUI clone() {
		final ModularGUI gui = new ModularGUI(this.internalName, this.command, this.permission);
		gui.addAll(this.guis);
		return gui;
	}

}