package me.fastfelix771.townywands.inventory;

import me.fastfelix771.townywands.lang.Language;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

// This class will enhance the managing of GUIs soon!
public class ModularGUI {

	private final String internalName;
	private final String title;
	private final int slots;
	private final String command;
	private final String permission;
	private final Language language;

	// maybe...? need to checkout the other classes...this class could compress down the InventoryBuilder and ItemBuilder into this one class :3
	private final ItemStack[] items;

	public ModularGUI(final String internalName, final String title, final String commandToOpen, final String permissionToOpen, final int slots, final Language lang, final ItemStack[] contents) {
		Validate.noNullElements(new Object[] { internalName, title, commandToOpen, permissionToOpen, slots, lang, contents }, "ModularGUI values cannot be null!");

		this.internalName = internalName;
		this.title = title;
		this.command = commandToOpen;
		this.permission = permissionToOpen;
		this.slots = slots;
		this.language = lang;
		this.items = contents;
	}

}