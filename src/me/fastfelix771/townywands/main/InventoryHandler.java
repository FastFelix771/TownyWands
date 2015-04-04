package me.fastfelix771.townywands.main;

import me.fastfelix771.townywands.inventory.InvAdmin;
import me.fastfelix771.townywands.inventory.InvPlayer;
import me.fastfelix771.townywands.inventory.InvResident;
import me.fastfelix771.townywands.inventory.TownyGUI;

/**
 * @author FastFelix771
 * @Desc
 *       Here you can set the guis, between the PlayerHandler will switch, if you are an dev and want to replace my gui with your own, go and change the correct variable :)
 */
public final class InventoryHandler {

	public static TownyGUI player = InvPlayer.gui;
	public static TownyGUI resident = InvResident.gui;
	public static TownyGUI townassistant = null;
	public static TownyGUI townvip = null;
	public static TownyGUI mayor = null;
	public static TownyGUI nationresident = null;
	public static TownyGUI nationassistant = null;
	public static TownyGUI nationvip = null;
	public static TownyGUI king = null;
	public static TownyGUI admin = InvAdmin.gui;

}