package de.fastfelix771.townywands.lang;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @description This class manages the translation of TownyWands GUI's!
 * @author FastFelix771
 * @specialThanks to mymemory.translated.net for its awesome API, without this feature wouldnt be possible with the same awesome translation-quality!
 */
@SuppressWarnings("all")
@Deprecated // TODO: Rewrite the Translator. Until this is done, there will be no auto-translation anymore.
public class Translator {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String QUOTA_REACHED = "MYMEMORY WARNING: YOU USED ALL AVAILABLE FREE TRANSLATIONS FOR TODAY. VISIT HTTP://MYMEMORY.TRANSLATED.NET/DOC/QUOTAREACHED TO TRANSLATE MORE";

    public static String translate(final Language from, final Language to, final String inputText) {
        String text = inputText;
        text = text.replace(" ", "%20");
        text = ChatColor.stripColor(text);
        text = text.replaceAll("&[0-9a-fklmnor]", ""); // Destroy colorcodes, because it doesnt work currently and decreases the translation quality.
        text = ChatColor.stripColor(text);
        final byte[] bytes = text.getBytes(UTF8);
        if (bytes.length > 500) return null;
        // MyMemory currently doesnt accept utf8-strings wich are bigger than 500 bytes.

        final String myMemory = "http://api.mymemory.translated.net/get?de=fastfelix771@gmail.com&langpair=" + from.getCode().replace("_", "-") + "|" + to.getCode().replace("_", "-") + "&mt=1&q=" + text;
        URL url = null;
        HttpURLConnection connection = null;
        int code = 0;
        BufferedReader input = null;
        String translated = null;

        try {
            // Connect to myMemory...
            url = new URL(myMemory);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000); // Should be enough, maybe ill add an config entry for that.
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            code = connection.getResponseCode();
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            // Do some checks to prevent errors

            if (code != 200) return null;

            // Now parse the input, we got from it.
            String line;
            final StringBuilder response = new StringBuilder();
            while ((line = input.readLine()) != null)
                response.append(line);
            input.close();
            translated = response.toString();
        }
        catch (final Exception e) {
            return null;
        }

        try {
            final JSONObject json = (JSONObject) new JSONParser().parse(translated);
            final JSONObject data = (JSONObject) json.get("responseData");
            translated = (String) data.get("translatedText");
        }
        catch (final Exception e) {
            return null;
        }

        return StringEscapeUtils.unescapeJava(translated);
    }

}