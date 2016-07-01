package de.fastfelix771.townywands.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import de.fastfelix771.townywands.packets.Version;
import de.fastfelix771.townywands.utils.Reflect;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.SneakyThrows;

/**
 * Utility class which guarantees UTF-8 loading & saving of YamlConfigurations!
 * @author FastFelix771
 */
public final class ConfigManager {

    @SneakyThrows
    public static YamlConfiguration loadYAML(@NonNull File file) {
        if (!file.exists()) return null;

        if(Reflect.getServerVersion().isNewerThan(Version.v1_6)) {
            return YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        }

        // On 1.6 & lower the Unicode bug may appear again... this will completely get fixed with the InGame-Editor.
        return YamlConfiguration.loadConfiguration(file);

    }

    @SneakyThrows
    public static void saveYAML(@NonNull YamlConfiguration config, @NonNull File file) {

        Method buildHeader = Reflect.getMethod(config.getClass().getDeclaredMethod("buildHeader"));

        DumperOptions yamlOptions = (DumperOptions) Reflect.getField(config.getClass().getDeclaredField("yamlOptions")).get(config);
        YamlRepresenter yamlRepresenter = (YamlRepresenter) Reflect.getField(config.getClass().getDeclaredField("yamlRepresenter")).get(config);
        Yaml yaml = (Yaml) Reflect.getField(config.getClass().getDeclaredField("yaml")).get(config);

        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        yamlOptions.setIndent(config.options().indent());
        yamlOptions.setAllowUnicode(true);

        String configHeader = (String) buildHeader.invoke(config);
        String yamlDump = yaml.dump(config.getValues(false));
        String blankConfig = (String) Reflect.getField(config.getClass().getDeclaredField("BLANK_CONFIG")).get(null);

        if (yamlDump.equalsIgnoreCase(blankConfig)) yamlDump = "";
        String data = StringEscapeUtils.unescapeJava(new String(new StringBuilder(configHeader).append(yamlDump).toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));

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