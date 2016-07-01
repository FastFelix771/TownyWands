package de.fastfelix771.townywands.api;

import java.util.Set;

import org.bukkit.inventory.Inventory;

import de.fastfelix771.townywands.dao.EntityInventory;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.utils.Utils;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModularInventory {

	private final EntityInventory dao;

	public static ModularInventory fromID(int id) {
		if(id == -1) return null;
		EntityInventory dao = TownyWands.getInstance().getDatabase().find(EntityInventory.class).where().eq("id", id).findUnique();
		return dao != null ? new ModularInventory(dao) : null;
	}

	public ModularInventory(@NonNull String title, int slots) {
		if(title.length() > 32 || !Utils.isValidSlotCount(slots)) {
			throw new ExceptionInInitializerError("Invalid title or slots given!");
		}

		EntityInventory entity = new EntityInventory();
		entity.setTitle(title);
		entity.setSlots(slots);

		TownyWands.getInstance().getDatabase().save(entity);
		this.dao = entity;
	}

	public ModularInventory(@NonNull Inventory inventory) {
		dao = null;
	}


	/**
	 * @param slots must be 9, 18, 27, 36, 45 or 54 (Minecraft Limitation)
	 */
	public void setSlots(int slots) {
		if(slots != -1 && Utils.isValidSlotCount(slots)) {
			dao.setSlots(slots);
		}
	}

	public void setEnabled(boolean enabled) {
		dao.setEnabled(enabled);
	}

	public void setTitle(@NonNull String title) {
		dao.setTitle(title);
	}

	public void setGUI(@NonNull ModularGUI gui) {
		dao.setGui(gui.getName());
	}

	public ModularGUI getGUI() {
		return ModularGUI.fromName(dao.getGui());
	}

	public String getTitle() {
		return dao.getTitle();
	}

	public int getSlots() {
		return dao.getSlots();
	}

	public boolean isEnabled() {
		return dao.isEnabled();
	}

	public int getID() {
		return dao.getId();
	}

	public Set<Object> getItems() {
		return null; // TODO: items getten
	}

}