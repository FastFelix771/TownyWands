package me.fastfelix771.townywands.inventory;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class TownyGUI {
	private final String title;
	private final Player owner;
	private final int slots;
	private Inventory inv;
	private final long id;
	// This pretty nice HashMap contains all the gui id's .___.
	// i recommend you to do not touch this...its just "public" for the devs who know what they are doing!
	public static final Map<Long, Inventory> ids = new HashMap<Long, Inventory>();

	/**
	 * @author FastFelix771
	 * @return this returns an 100% not used ID for your GUI!
	 */
	public static final long getNextID() {
		long gid = 1;
		while (ids.containsKey(gid)) {
			gid++;
		}
		return gid;
	}

	/**
	 * @author FastFelix771
	 * @param id
	 *            is the ID of the GUI you want to get
	 * @return the inventory from the bound id
	 */
	public static final Inventory getGUI(final long id) {
		if (!ids.containsKey(id)) {
			throw new IllegalArgumentException("ID " + id + " is not registered!");
		}
		return ids.get(id);
	}

	/**
	 * @author FastFelix771
	 * @param holder
	 *            is the inventory holder, usally you dont need it, just use null here
	 * @param name
	 *            is the title of the gui like "MyOwnTownyGUI"
	 * @param size
	 *            is the slotsize of the gui, for example: 9 = 9 slots
	 * @param id
	 *            is the ID of the gui, it must be unique or it will throw errors!
	 */
	public TownyGUI(final Player holder, final String name, final int size, final long id) {
		title = name;
		owner = holder;
		slots = size;
		if (ids.containsKey(id)) {
			throw new IllegalArgumentException("ID " + id + " is already used!");
		}
		this.id = id;
		build();
	}

	/**
	 * @author FastFelix771
	 * @return this returns an inventory of the gui itself, where you can add items etc.
	 */
	public final Inventory getInventory() {
		return inv;
	}

	// This just build the inventory and sets the ID, you can ignore it :)
	private final void build() {
		inv = Bukkit.createInventory(owner, slots, title);
		ids.put(id, inv);
	}

	/**
	 * @author FastFelix771
	 * @param player
	 *            will see the gui if its not already showing to him
	 */
	public final void show(final Player player) {
		if (!isShowing(player)) {
			player.openInventory(inv);
		}
	}

	/**
	 * @author FastFelix771
	 * @param player
	 *            when this player is looking at the gui, it will close itself.
	 */
	public final void close(final Player player) {
		if (isShowing(player)) {
			player.closeInventory();
		}
	}

	/**
	 * @author FastFelix771
	 * @param player
	 * @return true if the gui is showing to that player, false if not...obviously
	 */
	public final boolean isShowing(final Player player) {
		if (inv.getViewers().contains(player)) {
			return true;
		}
		return false;
	}
}