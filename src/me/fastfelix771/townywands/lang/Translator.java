package me.fastfelix771.townywands.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @description This class manages the automatic & asynchronously translation of TownyWands GUI's! (EXPERIMENTAL)
 * @author FastFelix771
 * @specialThanks to mymemory.translated.net for its awesome API, without this feature wouldnt be possible with the same awesome translation-quality!
 */
public class Translator {

	private static final String USER_AGENT = "Mozilla/5.0";
	private static final JSONParser parser = new JSONParser();
	private static final ExecutorService pool = Executors.newSingleThreadExecutor(); // Maybe ill make the threadcount configurable, but 1 thread should be 'nuff.

	public static String translate(final Language from, final Language to, final String inputText) {

		final Callable<String> callable = new Callable<String>() {

			@Override
			public String call() throws Exception {

				String text = inputText;
				text = text.replace(" ", "%20");
				final byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
				if (bytes.length > 500) {
					return null;
					// MyMemory currently doesnt accept utf8-strings wich are bigger than 500 bytes.
				}

				final String myMemory = "http://api.mymemory.translated.net/get?q=" + text + "&langpair=" + from.getCode().replace("_", "-") + "|" + to.getCode().replace("_", "-") + "&mt=1";
				System.out.println(myMemory);
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
					final JSONObject json = (JSONObject) parser.parse(translated);
					final JSONObject data = (JSONObject) json.get("responseData");
					translated = data.get("translatedText").toString();
				} catch (final ParseException e) {
					return null;
				}

				return translated;
			}
		};

		final FutureTask<String> task = new FutureTask<String>(callable);
		pool.execute(task);

		while (!task.isDone()) {
		}

		try {
			return task.get();
		} catch (InterruptedException | ExecutionException e) {
			return null;
		}
	}

}