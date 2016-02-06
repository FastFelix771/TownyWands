package me.fastfelix771.townywands.lang;

import java.lang.reflect.Field;
import me.fastfelix771.townywands.utils.Reflect;
import org.bukkit.entity.Player;

public enum Language {

    AUSTRALIAN_ENGLISH("Australian English", "en_AU"), AFRIKAANS("Afrikaans", "af_ZA"), ARABIC("العربية", "ar_SA"), BULGARIAN("Български", "bg_BG"), CATALAN("Català", "ca_ES"), CZECH("Čeština", "cs_CZ"), DANISH("Dansk", "da_DK"), GERMAN("Deutsch", "de_DE"), GREEK("Ελληνικά", "el_GR"), CANADIAN_ENGLISH("Canadian English", "en_CA"), ENGLISH("English", "en_US"), PIRATE_SPEAK("Pirate Speak", "en_PT"), ESPERANTO("Esperanto", "eo_EO"), SPANISH("Español", "es_ES"), FINNISH("Suomi", "fi_FI"), TAGALOG("Tagalog", "fil_PH"), FRENCH("Français", "fr_FR"), GALICIAN("Galego", "gl_ES"), HEBREW("עברית", "he_IL"), CROATIAN("Hrvatski", "hr_HR"), HUNGARIAN("Magyar", "hu_HU"), ARMENIAN("Հայերեն", "hy_AM"), BAHASA_INDONESIA("Bahasa Indonesia", "id_ID"), ICELANDIC("Íslenska", "is_IS"), ITALIAN("Italiano", "it_IT"), JAPANESE("日本語", "ja_JP"), GEORGIAN("ქართული", "ka_GE"), KOREAN("한국어", "ko_KR"), LITHUANIAN("Lietuvių", "lt_LT"), LATVIAN("Latviešu", "lv_LV"), MALTI("Malti", "mt_MT"), NORWEGIAN("Norsk", "nb_NO"), DUTCH("Nederlands", "nl_NL"), PORTUGUESE_BR("Português", "pt_BR"), PORTUGUESE_PT("Português", "pt_PT"), ROMANIAN("Română", "ro_RO"), RUSSIAN("Русский", "ru_RU"), SLOVENIAN("Slovenščina", "sl_SI"), SERBIAN("Српски", "sr_SP"), SWEDISH("Svenska", "sv_SE"), THAI("ภาษาไทย", "th_TH"), TURKISH("Türkçe", "tr_TR"), UKRAINIAN("Українська", "uk_UA"), VIETNAMESE("Tiếng Việt", "vi_VI"), SIMPLIFIED_CHINESE("简体中文", "zh_CN"), TRADITIONAL_CHINESE("繁體中文", "zh_TW"), POLISH("Polski", "pl_PL");

    private String name;
    private String code;

    Language(final String name, final String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }

    public static Language getLanguage(final Player p) {
        try {
            final Object player = Reflect.getNMSPlayer(p);
            final Field localeField = Reflect.getField(player.getClass().getDeclaredField("locale"));
            final String language = (String) localeField.get(player);
            return getByCode(language);
        }
        catch (final Exception e) {
            return Language.ENGLISH;
        }
    }

    public static Language getByCode(final String code) {
        for (final Language l : values())
            if (l.getCode().equalsIgnoreCase(code)) return l;
        return Language.ENGLISH;
    }

}