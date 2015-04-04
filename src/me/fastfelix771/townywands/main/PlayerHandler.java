package me.fastfelix771.townywands.main;

import me.fastfelix771.townywands.inventory.InvPlayer;
import me.fastfelix771.townywands.inventory.InvResident;
import me.fastfelix771.townywands.inventory.TownyGUI;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public final class PlayerHandler {

	/**
	 * @author FastFelix771
	 * @return The correct TownyGUI for the player
	 */
	// This is where the magic happens! It checks if the is resident mayor king or whatever and returns the correct gui to open!
	public static final Inventory getCorrectInventory(final Player player) {
		TownyGUI rightGUI = InvPlayer.gui;
		Resident res = null;
		try {
			res = TownyUniverse.getDataSource().getResident(player.getName());
		} catch (final NotRegisteredException e) {
			e.printStackTrace();
		}
		if (res.hasTown()) {
			rightGUI = InvResident.gui;
		} else {
			rightGUI = InvPlayer.gui; // Just to be on the safe side ._.
		}
		return rightGUI.getInventory();
	}
}