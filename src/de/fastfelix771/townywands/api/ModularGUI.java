package de.fastfelix771.townywands.api;

import java.util.HashSet;
import java.util.Set;

import de.fastfelix771.townywands.dao.EntityGUI;
import de.fastfelix771.townywands.dao.EntityInventory;
import de.fastfelix771.townywands.main.TownyWands;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

//TODO: Add complete JavaDocs for all methods!
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModularGUI {

	private final EntityGUI dao;

	public static ModularGUI fromName(String name) {
		if(name == null) return null;
		EntityGUI dao = TownyWands.getInstance().getDatabase().find(EntityGUI.class).where().eq("name", name).findUnique();
		return dao != null ? new ModularGUI(dao) : null;
	}	

	public static ModularGUI fromCommand(String command) {
		if(command == null) return null;
		EntityGUI dao = TownyWands.getInstance().getDatabase().find(EntityGUI.class).where().eq("command", command).findUnique();
		return dao != null ? new ModularGUI(dao) : null;
	}	
	
	public static Set<ModularGUI> loadAll() {
		Set<ModularGUI> guis = new HashSet<>();
		
		Set<EntityGUI> entities = TownyWands.getInstance().getDatabase().find(EntityGUI.class).findSet();
		for(EntityGUI entity : entities) {
			guis.add(ModularGUI.fromName(entity.getName()));
		}
		
		return guis;
	}

	public ModularGUI(@NonNull String name, @NonNull String command, @NonNull String permission) {
		if(TownyWands.getInstance().getDatabase().find(EntityGUI.class).where().eq("name", name).findUnique() != null) {
			throw new ExceptionInInitializerError("This name is already bound to a ModularGUI!");
		}

		EntityGUI entity = new EntityGUI();
		entity.setName(name);
		entity.setCommand(command);
		entity.setPermission(permission);

		TownyWands.getInstance().getDatabase().save(entity);
		this.dao = entity;
	}


	public void setCommand(@NonNull String command) {
		dao.setCommand(command);
	}

	public void setPermission(@NonNull String permission) {
		dao.setPermission(permission);
	}

	public String getCommand() {
		return dao.getCommand();
	}

	public String getPermission() {
		return dao.getPermission();
	}

	public String getName() {
		return dao.getName();
	}

	public Set<ModularInventory> getInventories() {
		Set<ModularInventory> inventories = new HashSet<>();
		Set<EntityInventory> entities = TownyWands.getInstance().getDatabase().find(EntityInventory.class).where().eq("gui", dao.getName()).findSet();

		for(EntityInventory entity : entities) {
			inventories.add(ModularInventory.fromID(entity.getId()));
		}

		return inventories;
	}

	/**
	 * @return active main inventory of the GUI (may be null)
	 */
	public ModularInventory getInventory() {
		EntityInventory entity = TownyWands.getInstance().getDatabase().find(EntityInventory.class).where().eq("gui", dao.getName()).eq("enabled", true).findUnique();
		return entity != null ? ModularInventory.fromID(entity.getId()) : null;
	}
	
	public void save() {
		TownyWands.getInstance().getDatabase().save(this.dao);
	}

}