package de.fastfelix771.townywands.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.io.NbtTextSerializer;

import de.fastfelix771.townywands.dao.EntityItem;
import de.fastfelix771.townywands.inventory.ItemWrapper;
import de.fastfelix771.townywands.lang.Language;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.utils.Base64;
import de.fastfelix771.townywands.utils.Compressor;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

// TODO: Add complete JavaDocs for all methods!
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
	public void setLore(List<String> list) {
		JSONArray json = new JSONArray();

		for(String line : list) {
			json.add(line);
		}

		dao.setLore(Base64.getInstance().print(Compressor.getInstance().compress(json.toJSONString().getBytes(StandardCharsets.UTF_8))));
	}

	@SuppressWarnings("unchecked")
	public List<String> getLore() {
		List<String> lore = new ArrayList<String>();
		JSONArray json = (JSONArray) JSONValue.parse(new String(Compressor.getInstance().decompress(Base64.getInstance().parse(dao.getLore())), StandardCharsets.UTF_8));
		lore.addAll(json);
		return lore;
	}

	public void setBinaryTag(NbtCompound tag) {
		if(tag == null) {
			dao.setTag(null);
			return;
		}

		dao.setTag(NbtTextSerializer.DEFAULT.serialize(tag));
	}

	@SneakyThrows(IOException.class)
	public NbtCompound getBinaryTag() {
		if(dao.getTag() == null) return null;
		return NbtTextSerializer.DEFAULT.deserializeCompound(dao.getTag());
	}

	public void setSlot(int slot) {
		if(!(slot < 0) && !(slot >= 54)) return;
		dao.setSlot(slot);
	}

	public int getSlot() {
		return dao.getSlot();
	}

	public int getID() {
		return dao.getId();
	}

	/**
	 * @return Semi-Cached Bukkit ItemStack from the given set of data.
	 * <br>
	 * <strong>Note</strong>: The ItemStack will be updated on every call of this method!
	 */
	public ItemStack toItemStack() {
		if(this.wrapper == null) this.wrapper = ItemWrapper.wrap(new ItemStack(this.getMaterial()));

		NbtCompound nbtTag;
		if((nbtTag = this.getBinaryTag()) != null) this.wrapper.setTag(nbtTag);

		this.wrapper.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.getDisplayName()));
		this.wrapper.setEnchanted(isEnchanted());
		this.wrapper.hideFlags(isHideFlags());
		this.wrapper.setAmount(getAmount());
		
		if(this.getLore() != null && !this.getLore().isEmpty()) {
			this.wrapper.setLore(this.getLore());
		}

		this.wrapper.setNBTKey("townywands_id", this.getID());
		return this.wrapper.getItem();
	}

	/**
	 * @param source The ItemStack, wich will replace the current data set.
	 * <br>
	 * The given ItemStack won't be manipulated in any way by this method!
	 */
	public void update(@NonNull ItemStack source) {
		this.wrapper = ItemWrapper.wrap(source);

		this.setBinaryTag(this.wrapper.getTag());

		this.setMaterial(source.getType());
		this.setMetaID(source.getDurability());
		this.setAmount(source.getAmount());
		this.setEnchanted(this.wrapper.hasNBTKey("ench"));
		this.setHideFlags(this.wrapper.hasNBTKey("HideFlags") && this.wrapper.getNBTKey("HideFlags", int.class) == 1);
		
		if(source.hasItemMeta()) {
			if(source.getItemMeta().hasDisplayName()) this.setDisplayName(source.getItemMeta().getDisplayName());
			if(source.getItemMeta().hasLore()) this.setLore(source.getItemMeta().getLore());
		}

	}

	public void save() {
		TownyWands.getInstance().getDatabase().save(this.dao);
	}

}