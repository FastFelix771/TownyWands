package me.fastfelix771.townywands.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import me.fastfelix771.townywands.utils.Reflect.Version;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This UpdateChecker shouldn't hurt the ToS of CurseForge as it only uses their API.
 * 
 * @author FastFelix771
 */
public class Update {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String BASE_URL = "https://api.curseforge.com/servermods/files?projectIds=";
    private static final String BASE_SEARCH_URL = "https://api.curseforge.com/servermods/projects?search=";
    private static final ExecutorService worker = Executors.newSingleThreadExecutor();
    private JavaPlugin plugin;
    private long id;
    private boolean canCheck;

    public Update(final JavaPlugin plugin) {
        Validate.notNull(plugin);
        this.plugin = plugin;
        this.canCheck = false;

        final String data = this.httpRequest(BASE_SEARCH_URL + plugin.getName().toLowerCase(), 10);
        final JSONArray array = (JSONArray) JSONValue.parse(data);

        if (array.isEmpty()) {
            send("Cannot find ID of plugin " + plugin.getName() + "!");
            return;
        }

        final JSONObject json = (JSONObject) array.get(0);

        final long id = Long.parseLong(json.get("id").toString());
        Validate.notNull(id);
        this.id = id;
        this.canCheck = true;
    }

    public Result check() {
        while (!this.canCheck) {
        }
        send("Checking for updates for plugin " + this.plugin.getName() + "!");

        final String data = this.httpRequest(BASE_URL + this.id, 10);
        Validate.notNull(data);
        final JSONArray array = (JSONArray) JSONValue.parse(data);

        if (array.isEmpty()) return null;

        final JSONObject json = (JSONObject) array.get(array.size() - 1);
        final String latestVersion = ((String) json.get("name")).replaceAll("[^v0-9.]", "").replace("v", "");
        final String latestURL = (String) json.get("downloadUrl");
        final String gameVersion = (String) json.get("gameVersion");

        final int latest = Integer.parseInt(latestVersion.replaceAll("[^0-9]", ""));
        final int current = Integer.parseInt(this.plugin.getDescription().getVersion().replaceAll("[^0-9]", ""));

        Result result = null;

        if (latest == current) result = new Result(State.NO_UPDATE, latestVersion, latestURL, gameVersion);
        else if (latest > current) result = new Result(State.UPDATE_FOUND, latestVersion, latestURL, gameVersion);
        else result = new Result(State.ERROR, latestVersion, latestURL, gameVersion);

        if (Version.fromString("v" + result.getGameVersion().replace(".", "_")) != Reflect.getServerVersion()) result = new Result(State.NO_UPDATE, latestVersion, latestURL, gameVersion);

        Validate.notNull(result);

        final State state = result.getState();

        if (state == State.UPDATE_FOUND) {
            send("Updates found for plugin " + this.plugin.getName() + "!");
            send("Current version: " + this.plugin.getDescription().getVersion() + ", latest version: " + latestVersion);
        }

        if (state == State.NO_UPDATE) send("No updates found for plugin " + this.plugin.getName() + "!");

        if (state == State.ERROR) send("Failed to check for updates for plugin " + this.plugin.getName() + "!");

        return result;
    }

    public void update(final String url, final String filename) {
        send("Updating plugin " + this.plugin.getName() + " to the newest version...");
        this.download(url, filename);
    }

    private void download(final String url_str, final String fileName) {
        OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;

        try {
            final URL url = new URL(url_str);
            out = new BufferedOutputStream(new FileOutputStream(this.plugin.getDataFolder().getParentFile().getAbsolutePath() + "/" + fileName));
            conn = url.openConnection();
            in = conn.getInputStream();
            final byte[] buffer = new byte[1024];
            int numRead;

            while ((numRead = in.read(buffer)) != -1)
                out.write(buffer, 0, numRead);
        }
        catch (final Exception exception) {
            send("Failed to update plugin " + this.plugin.getName());
            exception.printStackTrace();
        }
        finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            }
            catch (final IOException ioe) {
            }
        }
    }

    private String httpRequest(final String url_string, final int timeout) {
        final Callable<String> call = new Callable<String>() {

            @Override
            public String call() throws Exception {
                String temp = url_string;
                temp = temp.replace(" ", "%20");
                URL url = null;
                HttpURLConnection connection = null;
                int code = 0;
                BufferedReader input = null;
                String output = null;

                try {
                    url = new URL(temp);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(timeout * 1000);
                    connection.setRequestProperty("User-Agent", USER_AGENT);
                    connection.setRequestProperty("Accept-Charset", "UTF-8");
                    code = connection.getResponseCode();
                    input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    if (code != 200) return null;

                    String line;
                    final StringBuffer response = new StringBuffer();
                    while ((line = input.readLine()) != null)
                        response.append(line);
                    input.close();
                    output = response.toString();
                }
                catch (final IOException e) {
                    return null;
                }

                return output;
            }
        };

        final FutureTask<String> ft = new FutureTask<String>(call);
        worker.execute(ft);
        while (!ft.isDone()) {
        }

        try {
            return ft.get();
        }
        catch (final Exception e) {
            return null;
        }
    }

    private static void send(final String message) {
        Bukkit.getConsoleSender().sendMessage("§6[§aUpdater§6]§c " + message);
    }

    public static class Result {

        private final State state;
        private final String latestVersion;
        private final String latestURL;
        private final String gameVersion;

        public Result(final State state, final String latestVersion, final String latestURL, final String gameVersion) {
            this.state = state;
            this.latestURL = latestURL;
            this.latestVersion = latestVersion;
            this.gameVersion = gameVersion;
        }

        public String getGameVersion() {
            return this.gameVersion;
        }

        public State getState() {
            return this.state;
        }

        public String getLatestDownload() {
            return this.latestURL;
        }

        public String getLatest() {
            return this.latestVersion;
        }

    }

    private enum State {
        UPDATE_FOUND, NO_UPDATE, ERROR;
    }
}