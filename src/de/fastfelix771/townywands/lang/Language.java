package de.fastfelix771.townywands.lang;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import de.fastfelix771.townywands.utils.Reflect;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Language implements Serializable {

	AUSTRALIAN_ENGLISH("Australian English", "en_AU"), 
	AFRIKAANS("Afrikaans", "af_ZA"), 
	ARABIC("العربية", "ar_SA"), 
	BULGARIAN("Български", "bg_BG"), 
	CATALAN("Català", "ca_ES"), 
	CZECH("Čeština", "cs_CZ"), 
	DANISH("Dansk", "da_DK"), 
	GERMAN("Deutsch", "de_DE"), 
	GREEK("Ελληνικά", "el_GR"), 
	CANADIAN_ENGLISH("Canadian English", "en_CA"), 
	ENGLISH("English", "en_US"), 
	PIRATE_SPEAK("Pirate Speak", "en_PT"), 
	ESPERANTO("Esperanto", "eo_EO"), 
	SPANISH("Español", "es_ES"), 
	FINNISH("Suomi", "fi_FI"), 
	TAGALOG("Tagalog", "fil_PH"),
	FRENCH("Français", "fr_FR"), 
	GALICIAN("Galego", "gl_ES"),
	HEBREW("עברית", "he_IL"), 
	CROATIAN("Hrvatski", "hr_HR"),
	HUNGARIAN("Magyar", "hu_HU"),
	ARMENIAN("Հայերեն", "hy_AM"), 
	BAHASA_INDONESIA("Bahasa Indonesia", "id_ID"),
	ICELANDIC("Íslenska", "is_IS"),
	ITALIAN("Italiano", "it_IT"), 
	JAPANESE("日本語", "ja_JP"), 
	GEORGIAN("ქართული", "ka_GE"),
	KOREAN("한국어", "ko_KR"), 
	LITHUANIAN("Lietuvių", "lt_LT"),
	LATVIAN("Latviešu", "lv_LV"), 
	MALTI("Malti", "mt_MT"), 
	NORWEGIAN("Norsk", "nb_NO"),
	DUTCH("Nederlands", "nl_NL"), 
	PORTUGUESE_BR("Português", "pt_BR"), 
	PORTUGUESE_PT("Português", "pt_PT"), 
	ROMANIAN("Română", "ro_RO"), 
	RUSSIAN("Русский", "ru_RU"), 
	SLOVENIAN("Slovenščina", "sl_SI"), 
	SERBIAN("Српски", "sr_SP"), 
	SWEDISH("Svenska", "sv_SE"), 
	THAI("ภาษาไทย", "th_TH"), 
	TURKISH("Türkçe", "tr_TR"), 
	UKRAINIAN("Українська", "uk_UA"), 
	VIETNAMESE("Tiếng Việt", "vi_VI"), 
	SIMPLIFIED_CHINESE("简体中文", "zh_CN"), 
	TRADITIONAL_CHINESE("繁體中文", "zh_TW"), 
	POLISH("Polski", "pl_PL");

	@Getter
	private String name;

	@Getter
	private String code;

	public static Language getLanguage(Player p) {
		if(Reflect.getInstance().getClass("org.bukkit.entity.Player.Spigot") != null) {
			return getByCode(p.spigot().getLocale());
		}

		try {
			Object nms = Reflect.getInstance().getMethod(p.getClass(), "getHandle").invoke(p);
			Field localeField = Reflect.getInstance().getField(nms.getClass(), "locale");
			String language = (String) localeField.get(nms);
			return getByCode(language);
		} catch (Exception e) {
			return Language.ENGLISH;
		}
	}

	public static Language getByCode(String code) {
		for (Language l : values()) {
			if (l.getCode().equalsIgnoreCase(code)) return l;
		}
		
		return Language.ENGLISH;
	}

}