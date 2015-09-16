package me.fastfelix771.townywands.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import net.md_5.bungee.api.ChatColor;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @description This class manages the translation of TownyWands GUI's! (EXPERIMENTAL)
 * @author FastFelix771
 * @specialThanks to mymemory.translated.net for its awesome API, without this feature wouldnt be possible with the same awesome translation-quality!
 */
public class Translator {

	private static final String USER_AGENT = "Mozilla/5.0";

	public static String translate(final Language from, final Language to, final String inputText) {
		String text = inputText;
		text = text.replace(" ", "%20");
		text = ChatColor.stripColor(text);
		text = text.replaceAll("&[0-9a-fklmnor]", ""); // Destroy colorcodes, because it doesnt work currently and decreases the translation quality.
		text = ChatColor.stripColor(text);
		final byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
		if (bytes.length > 500) {
			return null;
			// MyMemory currently doesnt accept utf8-strings wich are bigger than 500 bytes.
		}

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

			if (code != 200) {
				return null;
			}

			// Now parse the input, we got from it.
			String line;
			final StringBuffer response = new StringBuffer();
			while ((line = input.readLine()) != null) {
				response.append(line);
			}
			input.close();
			translated = response.toString();
		} catch (final IOException e) {
			return null;
		}

		try {
			final JSONObject json = (JSONObject) new JSONParser().parse(translated);
			final JSONObject data = (JSONObject) json.get("responseData");
			translated = data.get("translatedText").toString();
		} catch (final ParseException e) {
			return null;
		}

		return translated;
	}

}