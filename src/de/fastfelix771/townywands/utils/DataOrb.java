package de.fastfelix771.townywands.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.DatatypeConverter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

@SuppressWarnings("all")
@NoArgsConstructor
public final class DataOrb implements Cloneable, Serializable {

    private static final long serialVersionUID = -1907821351368547289L;
    private Map<String, Object> map = new HashMap<>();

    public DataOrb(@NonNull Map<String, Object> map) {
        this.map = map;
    }

    public static DataOrb fromString(@NonNull String string) {
        return fromBytes(DatatypeConverter.parseBase64Binary(string));
    }

    @SneakyThrows
    public static DataOrb fromBytes(@NonNull byte[] bytes) {
        final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
        final Map map = (Map) in.readObject();
        in.close();
        return new DataOrb(map);
    }

    @SneakyThrows(IOException.class)
    public byte[] toBytes() {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(this.map);
        out.close();
        return bytes.toByteArray();
    }

    @Override
    public String toString() {
        return DatatypeConverter.printBase64Binary(this.toBytes());
    }

    @Override
    public DataOrb clone() {
        return fromString(this.toString());
    };

    public void put(String key, Object value) {
        this.map.put(key, value);
    }

    public void putIfAbsent(String key, Object value) {
        if (!containsKey(key)) put(key, value);
    }

    public boolean containsKey(String key) {
        return this.map.containsKey(key);
    }

    public void remove(String key) {
        this.map.remove(key);
    }

    public <T> T get(String key) {
        try {
            return (T) this.map.get(key);
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T get(String key, Class<T> returnType) {
        try {
            return (T) this.map.get(key);
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public Set<String> keySet() {
        return this.map.keySet();
    }

    public Set<Entry<String, Object>> entrySet() {
        return this.map.entrySet();
    }

}