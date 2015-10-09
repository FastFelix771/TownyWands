package me.fastfelix771.townywands.inventory;

import java.util.concurrent.ConcurrentHashMap;

import me.fastfelix771.townywands.lang.Language;
import me.fastfelix771.townywands.utils.DataBundle;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.Inventory;

// This class will enhance the managing of GUIs soon!
@SuppressWarnings("unused")
public class ModularGUI {

	private final String internalName;
	private final String command;
	private final String permission;
	private ConcurrentHashMap<Language, Inventory> guis;
	private final DataBundle data;

	// Add a language list.
	public ModularGUI(final String internalName, final String command, final String permission) {
		Validate.noNullElements(new Object[] { internalName, command, permission }, "ModularGUI values cannot be null!");

		this.internalName = internalName;
		this.command = command;
		this.permission = permission;
		this.data = new DataBundle(command, permission, null, null);
	}

	public DataBundle getData() {
		return this.data;
	}

	public void add(final Inventory inventory, final Language language) {

	}

	public Inventory get(final Language language) {
		return null;
	}

	public String getCommand() {
		return this.command;
	}

	public String getPermission() {
		return this.permission;
	}

}