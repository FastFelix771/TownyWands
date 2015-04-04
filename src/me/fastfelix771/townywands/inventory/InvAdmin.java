package me.fastfelix771.townywands.inventory;

import java.util.ArrayList;

import me.fastfelix771.townywands.utils.Util;

import org.bukkit.inventory.ItemStack;

public final class InvAdmin {

	private static InvAdmin instance;

	public static final InvAdmin getInstance() {
		return instance;
	}

	public static TownyGUI gui;

	public static final void createGUI() {
		final ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		gui = new TownyGUI(null, "§6§lAdmin GUI", 18, TownyGUI.getNextID(), Util.list2array(items));
	}
}