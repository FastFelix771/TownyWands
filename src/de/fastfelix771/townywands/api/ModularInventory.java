/*******************************************************************************
 * Copyright (C) 2017 Felix Drescher-Hackel
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.fastfelix771.townywands.api;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

import de.fastfelix771.townywands.dao.EntityInventory;
import de.fastfelix771.townywands.dao.EntityItem;
import de.fastfelix771.townywands.lang.Language;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.utils.Utils;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

//TODO: Add complete JavaDocs for all methods!
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModularInventory {

	private final EntityInventory dao;

	public static ModularInventory fromID(int id) {
		if(id == -1) return null;
		EntityInventory dao = TownyWands.getInstance().getDatabase().find(EntityInventory.class).where().eq("id", id).findUnique();
		return dao != null ? new ModularInventory(dao) : null;
	}
	
	public static Set<ModularInventory> loadAll() {
		Set<ModularInventory> inventories = new HashSet<>();
		
		Set<EntityInventory> entities = TownyWands.getInstance().getDatabase().find(EntityInventory.class).findSet();
		for(EntityInventory entity : entities) {
			inventories.add(ModularInventory.fromID(entity.getId()));
		}
		
		return inventories;
	}
	
	public static Set<ModularInventory> loadAll(ModularGUI gui) {
		Set<ModularInventory> inventories = new HashSet<>();
		
		Set<EntityInventory> entities = TownyWands.getInstance().getDatabase().find(EntityInventory.class).where().eq("gui", gui.getName()).findSet();
		for(EntityInventory entity : entities) {
			inventories.add(ModularInventory.fromID(entity.getId()));
		}
		
		return inventories;
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

	public Set<ModularItem> getItems(@NonNull Language language) {
		Set<ModularItem> items = new HashSet<>();

		Set<EntityItem> entities = TownyWands.getInstance().getDatabase().find(EntityItem.class).where().eq("inventory", getID()).eq("language", language).findSet();
		for(EntityItem entity : entities) {
			items.add(ModularItem.fromID(entity.getId()));
		}

		return items;
	}

	/**
	 * @return Fresh created Bukkit Inventory from the given data of this object with items in the specified language.
	 */
	public Inventory toInventory(@NonNull Language language) {
		Inventory inv = Bukkit.createInventory(null, getSlots(), ChatColor.translateAlternateColorCodes('&', getTitle()));

		for(ModularItem item : getItems(language)) {
			if(item.getSlot() >= getSlots()) continue;
			inv.setItem(item.getSlot(), item.toItemStack());
		}

		return inv;
	}
	
	public void save() {
		TownyWands.getInstance().getDatabase().save(this.dao);
	}

}
