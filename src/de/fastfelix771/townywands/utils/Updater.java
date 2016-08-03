package de.fastfelix771.townywands.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import de.fastfelix771.townywands.main.Debug;
import de.unitygaming.bukkit.vsign.Version;
import de.unitygaming.bukkit.vsign.invoker.Invoker;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * This Updater shouldn't hurt the ToS of CurseForge as it only uses their API.
 * @author FastFelix771
 */
@RequiredArgsConstructor
@AllArgsConstructor 
public final class Updater {

    private static final String URL_FETCH_ID = "https://api.curseforge.com/servermods/projects?search=%s";
    private static final String URL_FETCH_UPDATES = "https://api.curseforge.com/servermods/files?projectIds=%d";
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\bv((\\d.)+)?\\b).");

    private final Plugin plugin;
    private volatile long curseID = -1;

    public void check(@NonNull final Invoker<Result> invoker) {

        Executors.newSingleThreadExecutor().execute(new Runnable() {

            @Override
            public void run() {
                if(curseID == -1) {
                    try {
                        String raw = request(String.format(URL_FETCH_ID, plugin.getDescription().getName().toLowerCase()), 10);

                        JSONArray array = (JSONArray) JSONValue.parse(raw);
                        if(array == null || array.isEmpty()) throw new NullPointerException("array is null or empty");

                        JSONObject json = (JSONObject) array.get(0);
                        if(json == null || json.isEmpty()) throw new NullPointerException("json is null or empty");

                        long id = (long) json.get("id");
                        if(id != -1 && id != 0) curseID = id;
                        else throw new NullPointerException("id is null");
                    } catch(Exception e) {
                        e.printStackTrace();
                        Debug.msg(String.format("§cThe Updater has failed to fetch the Curse Project ID of %s!", plugin.getDescription().getName()), "§c".concat(e.getLocalizedMessage()));
                        return;
                    }
                }

                try {
                    String raw = request(String.format(URL_FETCH_UPDATES, curseID), 10);

                    JSONArray array = (JSONArray) JSONValue.parse(raw);
                    if(array == null || array.isEmpty()) throw new NullPointerException("array is null");

                    JSONObject json = (JSONObject) array.get(array.size() - 1);
                    if(json == null || json.isEmpty()) throw new NullPointerException("json is null");

                    String latestVersion = null;
                    Matcher matcher = VERSION_PATTERN.matcher((String) json.get("name"));
                    if(matcher.find()) {
                        latestVersion = matcher.toMatchResult().group(0).split(" ")[0]; // the version pattern isnt perfect yet, this is a small work-arround for one of its weaknesses
                    } else throw new IllegalStateException("version pattern did not match");
                    if(latestVersion == null || latestVersion.isEmpty()) throw new NullPointerException("latestVersion is null after pattern match");

                    String latestURL = (String) json.get("downloadUrl");
                    Version gameVersion = Version.fromString(((String) json.get("gameVersion")).replace('.', '_'));

                    int latest = Integer.parseInt(latestVersion.replaceAll("[^0-9]+", ""));
                    int current = Integer.parseInt(plugin.getDescription().getVersion().replaceAll("[^0-9]+", ""));

                    Result result = null;

                    if (latest == current) result = new Result(State.NO_UPDATE, latestVersion, latestURL, gameVersion);
                    else if (latest > current) result = new Result(State.UPDATE_FOUND, latestVersion, latestURL, gameVersion);
                    else result = new Result(State.ERROR, latestVersion, latestURL, gameVersion);

                    invoker.invoke(result);

                } catch(Throwable e) {
                    e.printStackTrace();
                    Debug.msg(String.format("§cThe Updater has failed to fetch updates for %s!", plugin.getDescription().getName()), "§c".concat(e.getLocalizedMessage()));
                    return;
                }

                Debug.msg(String.format("§aThe Updater has successfully fetched plugin updates for %s!", plugin.getDescription().getName()));

            }
        });
    }

    @SneakyThrows
    private static String request(@NonNull String webAddress, final int timeout) {
        URL url = new URL(webAddress);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);

        connection.setReadTimeout(timeout * 1000);
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setRequestProperty("Accept-Charset", "UTF-8");

        @Cleanup BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        return reader.readLine();
    }

    @Data
    public class Result {

        private final State state;
        private final String latestVersion;
        private final String latestURL;
        private final Version gameVersion;

    }

    public enum State {
        UPDATE_FOUND, NO_UPDATE, ERROR;
    }

}