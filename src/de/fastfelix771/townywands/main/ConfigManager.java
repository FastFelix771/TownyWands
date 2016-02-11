package de.fastfelix771.townywands.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class ConfigManager {

    @SneakyThrows
    public static FileConfiguration loadConfig(@NonNull File file) {
        if (!file.exists()) return null;
        return YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    }

    @SneakyThrows
    public static void saveConfig(@NonNull FileConfiguration config, @NonNull File file) {
        String data = config.saveToString(); // Custom saveToString! ALLOW_UNICODE scheint zu spacken.
        @Cleanup
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        writer.write(data);
    }

    @SneakyThrows(IOException.class)
    public static void saveResource(@NonNull String path, @NonNull File file, boolean overwrite) {
        if (file.exists() && !overwrite) return;
        @Cleanup
        InputStreamReader reader = new InputStreamReader(ConfigManager.class.getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8);
        @Cleanup
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);

        int i;
        while ((i = reader.read()) != -1) {
            writer.write(i);
        }

    }

}