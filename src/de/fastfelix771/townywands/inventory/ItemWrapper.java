package de.fastfelix771.townywands.inventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.fastfelix771.townywands.utils.Reflect;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE) 
@SuppressWarnings("all")
public class ItemWrapper {

	@NonNull
	@Getter
	private ItemStack item;

	public static ItemWrapper wrap(@NonNull ItemStack item) {
		return new ItemWrapper(item);
	}

	// FEATURES //

	public void setDisplayName(String string) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', string));
		item.setItemMeta(meta);
	}

	public void setAmount(int amount) {
		item.setAmount(amount);
	}

	public void setMaterial(Material mat) {
		item.setType(mat);
	}

	public void setID(int id) {
		item.setTypeId(id);
	}

	public void setMetaID(short meta) {
		item.setDurability(meta);
	}

	public void setLore(String... strings) {
		ItemMeta meta = item.getItemMeta();
		for (int i = 0; i < strings.length; i++) {
			strings[i] = ChatColor.translateAlternateColorCodes('&', strings[i]);
		}
		meta.setLore(Arrays.asList(strings));
		item.setItemMeta(meta);
	}

	public void setLore(List<String> strings) {
		ItemMeta meta = item.getItemMeta();
		for (int i = 0; i < strings.size(); i++) {
			strings.set(i, ChatColor.translateAlternateColorCodes('&', strings.get(i)));
		}
		meta.setLore(strings);
		item.setItemMeta(meta);
	}

	public void setLore(Set<String> strings) {
		setLore(strings.toArray(new String[strings.size()]));
	}

	public void addLore(String... strings) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		for (int i = 0; i < strings.length; i++) {
			strings[i] = ChatColor.translateAlternateColorCodes('&', strings[i]);
		}
		lore.addAll(Arrays.asList(strings));
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	@SneakyThrows
	public void hideFlags(boolean hide) {
		final Object tag = getTag();
		final Method setString = Reflect.getInstance().getMethod(Reflect.NBTTagCompound, "setString", String.class, String.class);
		setString.invoke(tag, "HideFlags", (hide ? "1" : "0"));
		setTag(tag);
	}

	@SneakyThrows
	public void setEnchanted(boolean enchanted) {
		Object tag = getTag();
		Method set = Reflect.getInstance().getMethod(Reflect.NBTTagCompound, "set", String.class, Reflect.NBTBase);
		Method hasKey = Reflect.getInstance().getMethod(Reflect.NBTTagCompound, "hasKey", String.class);
		Method remove = Reflect.getInstance().getMethod(Reflect.NBTTagCompound, "remove", String.class);
		if (enchanted) {
			set.invoke(tag, "ench", Reflect.getInstance().getConstructor(Reflect.getInstance().getNMSClass("NBTTagList")).newInstance());
		}
		else {
			if ((boolean) hasKey.invoke(tag, "ench")) remove.invoke(tag, "ench");
		}
		setTag(tag);
	}

	@SneakyThrows
	public void setNBTKey(@NonNull String key, @NonNull Object value) {
		Method setString = Reflect.getInstance().getMethod(Reflect.NBTTagCompound,"setString", String.class, String.class);
		Object tag = getTag();
		setString.invoke(tag, key, value);
		setTag(tag);
	}

	@SuppressWarnings("unchecked")
	@SneakyThrows
	public <T> T getNBTKey(@NonNull String key) {
		Method getString = Reflect.getInstance().getMethod(Reflect.NBTTagCompound, "getString", String.class);
		Object tag = getTag();

		String data = (String) getString.invoke(tag, key);
		if (data == null || data.trim().isEmpty()) return null;

		return (T) data;
	}

	@SneakyThrows
	public boolean hasNBTKey(@NonNull String key) {
		Method hasKey = Reflect.getInstance().getMethod(Reflect.NBTTagCompound, "hasKey", String.class);
		return (boolean) hasKey.invoke(getTag(), key);
	}

	@SuppressWarnings("unchecked")
	@SneakyThrows
	public <T> T getNBTKey(@NonNull String key, @NonNull Class<T> returnType) {
		return (T) getNBTKey(key);
	}

	// TODO: Add remove method.

	@SneakyThrows
	public void setTag(@NonNull Object tag) {
		Object nms = Reflect.getInstance().getNMSItem(item);
		final Method setTag = Reflect.getInstance().getMethod(Reflect.ItemStack, "setTag", Reflect.NBTTagCompound);
		setTag.invoke(nms, tag);
		this.item = Reflect.getInstance().getBukkitItem(nms);
	}

	@SneakyThrows
	public Object getTag() {
		Object nms = Reflect.getInstance().getNMSItem(item);
		Constructor<?> NBTTagCompound = Reflect.getInstance().getConstructor(Reflect.NBTTagCompound);
		Method hasTag = Reflect.getInstance().getMethod(Reflect.ItemStack, "hasTag");
		Method getTag = Reflect.getInstance().getMethod(Reflect.ItemStack, "getTag");
		Object tag = ((boolean) hasTag.invoke(nms) ? getTag.invoke(nms) : NBTTagCompound.newInstance());
		return tag;
	}

}