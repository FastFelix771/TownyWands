package me.fastfelix771.townywands.inventory;

public final class InvPlayer {

	private static InvPlayer instance;

	public static final InvPlayer getInstance() {
		return instance;
	}

	public static TownyGUI gui;

	public static final void createGUI() {
		gui = new TownyGUI(null, "§6§lPlayer GUI", 18, TownyGUI.getNextID());
	}

}