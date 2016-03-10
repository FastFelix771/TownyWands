package de.fastfelix771.townywands.inventory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import de.fastfelix771.townywands.utils.DataOrb;
import de.fastfelix771.townywands.utils.Reflect;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE) @SuppressWarnings("all")
public final class ItemWrapper implements Cloneable {

    // The PREFIX should prevent errors with minecraft itself, mods and other plugins.
    private static final String PREFIX = "townywands_";

    @NonNull
    @Getter
    private ItemStack item;

    public static ItemWrapper wrap(@NonNull ItemStack item) {
        return new ItemWrapper(item);
    }

    public static ItemWrapper fromString(@NonNull String string) {
        try (ByteArrayInputStream bytes = new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(string)); BukkitObjectInputStream inputstream = new BukkitObjectInputStream(bytes)) {
            final ItemStack item = (ItemStack) inputstream.readObject();
            inputstream.close();
            return wrap(item);
        }
        catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream(); BukkitObjectOutputStream output = new BukkitObjectOutputStream(bytes)) {
            output.writeObject(item);
            output.close();
            return DatatypeConverter.printBase64Binary(bytes.toByteArray());
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ItemWrapper clone() {
        return fromString(this.toString());
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
        final Method setString = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("setString", String.class, String.class));
        setString.invoke(tag, "HideFlags", (hide ? "1" : "0"));
        setTag(tag);
    }

    @SneakyThrows
    public void setEnchanted(boolean enchanted) {
        Object tag = getTag();
        Method set = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("set", String.class, Reflect.NBTBase));
        Method hasKey = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("hasKey", String.class));
        Method remove = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("remove", String.class));
        if (enchanted) {
            set.invoke(tag, "ench", Reflect.getConstructor(Reflect.getNMSClass("NBTTagList")).newInstance());
        }
        else {
            if ((boolean) hasKey.invoke(tag, "ench")) remove.invoke(tag, "ench");
        }
        setTag(tag);
    }

    @SneakyThrows
    public void setValue(@NonNull String key, @NonNull Serializable value) {
        Method setString = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("setString", String.class, String.class));

        DataOrb orb = new DataOrb();
        orb.put("data", value);
        String data = orb.toString();

        if (data == null || data.trim().isEmpty()) return;

        Object tag = getTag();
        setString.invoke(tag, new StringBuilder(PREFIX).append(key).toString(), data);
        setTag(tag);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T> T getValue(@NonNull String key) {
        Method getString = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("getString", String.class));
        Object tag = getTag();

        String data = (String) getString.invoke(tag, new StringBuilder(PREFIX).append(key).toString());
        if (data == null || data.trim().isEmpty()) return null;

        DataOrb orb = DataOrb.fromString(data);
        Serializable value = orb.get("data");

        return (T) value;
    }

    @SneakyThrows
    public boolean hasValue(@NonNull String key) {
        Method hasKey = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("hasKey", String.class));
        return (boolean) hasKey.invoke(getTag(), new StringBuilder(PREFIX).append(key).toString()) && getValue(key) != null;
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T> T getValue(@NonNull String key, @NonNull Class<T> returnType) {
        return (T) getValue(key);
    }

    // TODO: Add remove method.

    // INTERNAL //

    @SneakyThrows
    private void setTag(@NonNull Object tag) {
        Object nms = Reflect.getNMSItem(item);
        final Method setTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("setTag", Reflect.NBTTagCompound));
        setTag.invoke(nms, tag);
        this.item = Reflect.getBukkitItem(nms);
    }

    @SneakyThrows
    private Object getTag() {
        Object nms = Reflect.getNMSItem(item);
        Constructor<?> NBTTagCompound = Reflect.getConstructor(Reflect.NBTTagCompound);
        Method hasTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("hasTag"));
        Method getTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("getTag"));
        Object tag = ((boolean) hasTag.invoke(nms) ? getTag.invoke(nms) : NBTTagCompound.newInstance());
        return tag;
    }

}