package me.fastfelix771.townywands.main;

import me.fastfelix771.townywands.inventory.TownyGUI;
import me.fastfelix771.townywands.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.palmergames.bukkit.towny.object.Resident;

public final class PlayerHandler implements Listener {

	/**
	 * @author FastFelix771
	 * @return The correct TownyGUI for the player
	 */
	/*
	 * This is where the magic happens! It checks if the is resident mayor king or whatever and returns the correct gui to open!
	 * Its of course not done yet...but in my testings, it worked great! :)
	 */
	public static final TownyGUI getCorrectGUI(final Player player) {
		TownyGUI gui = null;
		final Resident res = Util.getResident(player);
		if (res.hasTown()) {
			gui = InventoryHandler.resident;
		} else {
			gui = InventoryHandler.player;
		}
		return gui;
	}

	/*
	 * This method is for now only for testing purposes, im working on an smart checkFor system :)
	 * Of course you can use your own system on your GUIs, your creativity is asked....
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public final void onInventoryClick(final InventoryClickEvent e) {
		if (Mainclass.allowlisteners) {
			e.setCancelled(true);
			final Player p = (Player) e.getWhoClicked();
			Bukkit.dispatchCommand(p, "t leave");
			p.closeInventory();
			Util.push("§4§lTown left", "§cNow you are a lone wolf...", 3, 5, 5, p);
		}
	}
}