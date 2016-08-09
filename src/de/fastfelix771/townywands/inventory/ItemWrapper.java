package de.fastfelix771.townywands.inventory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

import de.fastfelix771.townywands.utils.Reflect;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE) 
public class ItemWrapper {

	@Getter
	private final ItemStack item;

	@SneakyThrows( {InvocationTargetException.class, IllegalAccessException.class} )
	public static ItemWrapper wrap(@NonNull ItemStack source) {
		if(!MinecraftReflection.isCraftItemStack(source)) {
			return new ItemWrapper((ItemStack) Reflect.getMethod(MinecraftReflection.getCraftItemStackClass(), "asCraftCopy", ItemStack.class).invoke(Reflect.STATIC, source));
		}

		return new ItemWrapper(source);
	}


	public void setDisplayName(@NonNull String displayName) {
		ItemMeta meta = this.item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
		this.item.setItemMeta(meta);
	}

	public void setAmount(int amount) {
		this.item.setAmount((amount >= 1 && amount <= 64) ? amount : 1);
	}

	public void setMaterial(@NonNull Material mat) {
		this.item.setType(mat);
	}

	public void setMetaID(int id) {
		this.item.setDurability((short) id);
	}

	public void setLore(@NonNull String... lore) {
		ItemMeta meta = this.item.getItemMeta();

		for (int i = 0; i < lore.length; i++) {
			lore[i] = ChatColor.translateAlternateColorCodes('&', lore[i]);
		}

		meta.setLore(Arrays.asList(lore));
		this.item.setItemMeta(meta);
	}

	public void setLore(@NonNull List<String> strings) {
		ItemMeta meta = this.item.getItemMeta();

		for (int i = 0; i < strings.size(); i++) {
			strings.set(i, ChatColor.translateAlternateColorCodes('&', strings.get(i)));
		}

		meta.setLore(strings);
		this.item.setItemMeta(meta);
	}

	public void addLore(@NonNull String... strings) {
		ItemMeta meta = this.item.getItemMeta();
		List<String> lore = meta.getLore();

		for (int i = 0; i < strings.length; i++) {
			strings[i] = ChatColor.translateAlternateColorCodes('&', strings[i]);
		}

		lore.addAll(Arrays.asList(strings));
		meta.setLore(lore);
		this.item.setItemMeta(meta);
	}

	public void hideFlags(boolean hide) {
		this.getTag().put("HideFlags", hide ? 1 : 0);
	}

	public void setEnchanted(boolean enchanted) {
		if(enchanted) {
			if(!hasNBTKey("ench")) {
				setNBTKey("ench", NbtFactory.ofList("ench"));
			}
			return;
		}

		if(hasNBTKey("ench")) {
			removeNBTKey("ench");
		}
	}

	public void setNBTKey(@NonNull String key, Object value) {
		this.getTag().putObject(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getNBTKey(@NonNull String key) {
		return (T) this.getTag().getObject(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T getNBTKey(@NonNull String key, @NonNull Class<T> returnType) {
		return (T) getNBTKey(key);
	}

	public boolean hasNBTKey(@NonNull String key) {
		return this.getTag().containsKey(key);
	}

	public void removeNBTKey(@NonNull String key) {
		this.getTag().remove(key);
	}

	public void setTag(@NonNull NbtCompound tag) {
		NbtFactory.setItemTag(this.item, tag);
	}

	public NbtCompound getTag() {		
		return NbtFactory.asCompound(NbtFactory.fromItemTag(this.item));
	}

}