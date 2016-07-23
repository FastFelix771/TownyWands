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

import de.fastfelix771.townywands.utils.Reflect;
import de.unitygaming.bukkit.vsign.Version;
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

		if(Version.getCurrent().isNewerThan(Version.v1_6)) {
			return YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
		}

		return YamlConfiguration.loadConfiguration(file);

	}

	@SneakyThrows
	public static void saveYAML(@NonNull YamlConfiguration config, @NonNull File file) {

		Method buildHeader = Reflect.getInstance().getMethod(config.getClass(), "buildHeader");

		DumperOptions yamlOptions = (DumperOptions) Reflect.getInstance().getField(config.getClass(), "yamlOptions").get(config);
		YamlRepresenter yamlRepresenter = (YamlRepresenter) Reflect.getInstance().getField(config.getClass(), "yamlRepresenter").get(config);
		Yaml yaml = (Yaml) Reflect.getInstance().getField(config.getClass(), "yaml").get(config);

		yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

		yamlOptions.setIndent(config.options().indent());
		yamlOptions.setAllowUnicode(true);

		String configHeader = (String) buildHeader.invoke(config);
		String yamlDump = yaml.dump(config.getValues(false));
		String blankConfig = (String) Reflect.getInstance().getField(config.getClass(), "BLANK_CONFIG").get(null);

		if (yamlDump.equalsIgnoreCase(blankConfig)) yamlDump = "";
		String data = StringEscapeUtils.unescapeJava(new String(new StringBuilder(configHeader).append(yamlDump).toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));

		try(OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			writer.write(data);
		} catch(IOException e) {
			e.printStackTrace();
		}

	}

	public static void saveResource(@NonNull String path, @NonNull File file, boolean overwrite) {
		if (file.exists() && !overwrite) return;

		try(InputStreamReader reader = new InputStreamReader(ConfigManager.class.getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8); OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {

			int i;
			while ((i = reader.read()) != -1) {
				writer.write(i);
			}

		} catch(IOException e) {
			e.printStackTrace();
		}

	}

}