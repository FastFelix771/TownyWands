package de.fastfelix771.townywands.api;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import de.fastfelix771.townywands.dao.EntityItem;
import de.fastfelix771.townywands.inventory.ItemWrapper;
import de.fastfelix771.townywands.lang.Language;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.utils.Base64;
import de.fastfelix771.townywands.utils.Compressor;
import de.fastfelix771.townywands.utils.Serializer;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModularItem {

	private final EntityItem dao;
	private ItemWrapper wrapper;

	public static ModularItem fromID(int id) {
		if(id == -1) return null;
		EntityItem dao = TownyWands.getInstance().getDatabase().find(EntityItem.class).where().eq("id", id).findUnique();
		return dao != null ? new ModularItem(dao) : null;
	}

	public ModularItem(@NonNull String displayName, @NonNull Material material, short metaID) {
		EntityItem entity = new EntityItem();
		entity.setDisplayname(displayName);
		entity.setMaterial(material);
		entity.setMetaID(metaID);


		TownyWands.getInstance().getDatabase().save(entity);
		this.dao = entity;
	}

	public static Set<ModularItem> loadAll() {
		Set<ModularItem> items = new HashSet<>();

		Set<EntityItem> entities = TownyWands.getInstance().getDatabase().find(EntityItem.class).findSet();
		for(EntityItem entity : entities) {
			items.add(ModularItem.fromID(entity.getId()));
		}

		return items;
	}

	public static Set<ModularItem> loadAll(ModularInventory inv) {
		Set<ModularItem> items = new HashSet<>();

		Set<EntityItem> entities = TownyWands.getInstance().getDatabase().find(EntityItem.class).where().eq("inventory", inv.getID()).findSet();
		for(EntityItem entity : entities) {
			items.add(ModularItem.fromID(entity.getId()));
		}

		return items;
	}

	public ModularItem(@NonNull ItemStack item) {
		dao = null;
	}


	public void setAmount(int amount) {
		if(!(amount <= 0) && !(amount > 64)) {
			dao.setAmount(amount);
		}
	}

	public int getAmount() {
		return dao.getAmount();
	}

	public void setDisplayName(@NonNull String displayName) {
		dao.setDisplayname(displayName);
	}

	public String getDisplayName() {
		return dao.getDisplayname();
	}

	public void setHideFlags(boolean hideFlags) {
		dao.setHideFlags(hideFlags);
	}

	public boolean isHideFlags() {
		return dao.isHideFlags();
	}

	public void setEnchanted(boolean enchanted) {
		dao.setEnchanted(enchanted);
	}

	public boolean isEnchanted() {
		return dao.isEnchanted();
	}

	public void setLanguage(Language language) {
		dao.setLanguage(language);
	}

	public Language getLanguage() {
		return dao.getLanguage();
	}

	public void setMaterial(Material material) {
		dao.setMaterial(material);
	}

	public Material getMaterial() {
		return dao.getMaterial();
	}

	public void setInventory(int id) {
		dao.setInventory(id);
	}

	public int getInventory() {
		return dao.getInventory();
	}

	public void setMetaID(short metaID) {
		if(metaID == -1) {
			dao.setMetaID((short) 0);
			return;
		}

		dao.setMetaID(metaID);
	}

	public short getMetaID() {
		return dao.getMetaID();
	}

	@SuppressWarnings("unchecked")
	public void setCommands(String... commands) {
		JSONArray json = new JSONArray();

		for(String line : commands) {
			json.add(line);
		}

		dao.setCommands(Base64.getInstance().print(Compressor.getInstance().compress(json.toJSONString().getBytes(StandardCharsets.UTF_8))));
	}

	@SuppressWarnings("unchecked")
	public Set<String> getCommands() {
		Set<String> commands = new HashSet<>();
		JSONArray json = (JSONArray) JSONValue.parse(new String(Compressor.getInstance().decompress(Base64.getInstance().parse(dao.getCommands())), StandardCharsets.UTF_8));
		commands.addAll(json);
		return commands;
	}

	@SuppressWarnings("unchecked")
	public void setConsoleCommands(String... consoleCommands) {
		JSONArray json = new JSONArray();

		for(String line : consoleCommands) {
			json.add(line);
		}

		dao.setConsoleCommands(Base64.getInstance().print(Compressor.getInstance().compress(json.toJSONString().getBytes(StandardCharsets.UTF_8))));
	}

	@SuppressWarnings("unchecked")
	public Set<String> getConsoleCommands() {
		Set<String> consoleCommands = new HashSet<>();
		JSONArray json = (JSONArray) JSONValue.parse(new String(Compressor.getInstance().decompress(Base64.getInstance().parse(dao.getConsoleCommands())), StandardCharsets.UTF_8));
		consoleCommands.addAll(json);
		return consoleCommands;
	}

	@SuppressWarnings("unchecked")
	public void setLore(String... lore) {
		JSONArray json = new JSONArray();

		for(String line : lore) {
			json.add(line);
		}

		dao.setLore(Base64.getInstance().print(Compressor.getInstance().compress(json.toJSONString().getBytes(StandardCharsets.UTF_8))));
	}

	@SuppressWarnings("unchecked")
	public Set<String> getLore() {
		Set<String> lore = new HashSet<>();
		JSONArray json = (JSONArray) JSONValue.parse(new String(Compressor.getInstance().decompress(Base64.getInstance().parse(dao.getLore())), StandardCharsets.UTF_8));
		lore.addAll(json);
		return lore;
	}

	public void setBinaryTag(Object tag) {
		if(tag == null) {
			dao.setTag(null);
			return;
		}

		dao.setTag(Base64.getInstance().print(Compressor.getInstance().compress(Serializer.getInstance().serialize((Serializable) tag))));
	}

	public Object getBinaryTag() {
		if(dao.getTag() == null) return null;
		return Serializer.getInstance().deserialize(Compressor.getInstance().decompress(Base64.getInstance().parse(dao.getTag())));
	}

	public void setSlot(int slot) {
		if(!(slot > 0 && slot < 54)) return;
		dao.setSlot(slot);
	}

	public int getSlot() {
		return dao.getSlot();
	}

	public int getID() {
		return dao.getId();
	}

	/**
	 * @return Semi-Cached Bukkit ItemStack built from the saved data of this object.
	 */
	public ItemStack toItemStack() { //TODO: add NBT Key "townywands_id" to items dao id.
		if(wrapper == null) wrapper = ItemWrapper.wrap(new ItemStack(Material.STONE));

		if(getBinaryTag() != null) wrapper.setTag(getBinaryTag());
		wrapper.setMaterial(getMaterial());
		wrapper.setMetaID(getMetaID());
		wrapper.setDisplayName(ChatColor.translateAlternateColorCodes('&', getDisplayName()));
		wrapper.setEnchanted(isEnchanted());
		wrapper.hideFlags(isHideFlags());
		wrapper.setAmount(getAmount());
		if(getLore() != null && !getLore().isEmpty()) {
			for(String line : getLore()) {
				wrapper.addLore(ChatColor.translateAlternateColorCodes('&', line));
			}
		}

		wrapper.setNBTKey("townywands_id", getID());
		return wrapper.getItem();
	}

	/**
	 * @param source The ItemStack, wich data will be merged into this object.
	 */
	public void update(@NonNull ItemStack source) {
		wrapper = ItemWrapper.wrap(source);

		setBinaryTag(wrapper.getTag());

		if(source.hasItemMeta() && source.getItemMeta().hasDisplayName()) {
			setDisplayName(source.getItemMeta().getDisplayName());
			if(source.getItemMeta().hasLore()) setLore(source.getItemMeta().getLore().toArray(new String[source.getItemMeta().getLore().size()]));
		}

		setEnchanted(wrapper.hasNBTKey("ench"));
		setHideFlags(wrapper.hasNBTKey("HideFlags") && wrapper.getNBTKey("HideFlags", int.class) == 1);
		setAmount(source.getAmount());
		setMaterial(source.getType());
		setMetaID(source.getDurability());
	}

	public void save() {
		TownyWands.getInstance().getDatabase().save(this.dao);
	}

}