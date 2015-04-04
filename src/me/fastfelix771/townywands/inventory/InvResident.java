package me.fastfelix771.townywands.inventory;

import java.util.ArrayList;

import me.fastfelix771.townywands.utils.Util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class InvResident {

	private static InvResident instance;

	public static final InvResident getInstance() {
		return instance;
	}

	public static TownyGUI gui;

	public static final void createGUI() {
		final ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		items.add(Util.createItem("§c§lLeave town", "§7Be careful! Youll leave your town ._.", 1, Material.LAVA));
		gui = new TownyGUI(null, "§6§lResident GUI", 18, TownyGUI.getNextID(), Util.list2array(items));
	}
}