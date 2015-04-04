package me.fastfelix771.townywands.inventory;

public final class InvResident {

	private static InvResident instance;

	public static final InvResident getInstance() {
		return instance;
	}

	public static TownyGUI gui;

	public static final void createGUI() {
		gui = new TownyGUI(null, "§6§lResident GUI", 18, TownyGUI.getNextID());
	}
}