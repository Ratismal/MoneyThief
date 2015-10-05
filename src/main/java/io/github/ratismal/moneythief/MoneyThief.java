package io.github.ratismal.moneythief;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.ratismal.moneythief.handler.CommandHandler;
import io.github.ratismal.moneythief.handler.EntityKillerListener;
import io.github.ratismal.moneythief.handler.PlayerKillerListener;
import io.github.ratismal.moneythief.config.Config;
import io.github.ratismal.moneythief.util.FanfarePlayer;
import io.github.ratismal.moneythief.util.ProcessMessage;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mcstats.Metrics;
//import org.mcstats.Metrics;

public class MoneyThief extends JavaPlugin {

    private Config pluginconfig;

	private String newVersionTitle = "";
	private double newVersion = 0;
	private double currentVersion = 0;
	private String currentVersionTitle = "";

	public static MoneyThief plugin;
	public Logger log = getLogger();
	public Economy econ = null;
	//public Map<String, Object> configValues = new HashMap<String, Object>();
	//public Map<String, Object> mobValues = new HashMap<String, Object>();
	FanfarePlayer music;

	public File songOneData = null;
	public FileConfiguration song1 = null;
	public File songTwoData = null;
	public FileConfiguration song2 = null;
	public File songThreeData = null;
	public FileConfiguration song3 = null;

	@Override
	public void onEnable() {

		//saveCustomConfig();
		//log.info("Running on version " + getDescription().getVersion());
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MoneyThief] Plugin by Ratismal");
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MoneyThief] Check for updates at: ");
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MoneyThief]" + ChatColor.AQUA + " http://dev.bukkit.org/bukkit-plugins/moneythief/");
		plugin = this;

		PluginManager pm = this.getServer().getPluginManager();

		this.saveDefaultSongOne();
		this.saveDefaultSongTwo();
		this.saveDefaultSongThree();
		this.saveDefaultConfig();
		getConfig();
		getSongOne();
		getSongTwo();
		getSongThree();

		this.pluginconfig = new Config(this, getConfig());

		if (!setupEconomy() ) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MoneyThief]" + ChatColor.YELLOW + " Hooked onto Vault!");
		}
		pm.registerEvents(new PlayerKillerListener(this), this);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MoneyThief] Player Listener Enabled");
		pm.registerEvents(new EntityKillerListener(this), this);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MoneyThief] Entity Listener Enabled");

		//reloadCustomConfig();

		currentVersionTitle = getDescription().getVersion().split("-")[0];
        currentVersion = Double.valueOf(currentVersionTitle.replaceFirst("\\.", ""));

		if (getConfig().getBoolean("metrics")) {
			try {
				Metrics metrics = new Metrics(this);
				metrics.start();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MoneyThief] Metrics started");
			} catch (IOException e) {
				// Failed to submit the stats :-(
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MoneyThief] Metrics failed to start");
			}
		}


		//perform update check
		this.getServer().getScheduler().runTask(this, new Runnable() {

			@Override
			public void run() {
				// Programmatically set the default permission value cause Bukkit doesn't handle plugin.yml properly for Load order STARTUP plugins
				org.bukkit.permissions.Permission perm = getServer().getPluginManager().getPermission("moneythief.update");
				if (perm == null)
				{
					perm = new org.bukkit.permissions.Permission("moneythief.update");
					perm.setDefault(PermissionDefault.OP);
					plugin.getServer().getPluginManager().addPermission(perm);
				}
				perm.setDescription("Allows a user or the console to check for moneythief updates");

				getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

					@Override
					public void run() {
						if (getServer().getConsoleSender().hasPermission("moneythief.update") && getConfig().getBoolean("update-check", true)) {
							try {
								log.info("Running update checker...");
								newVersion = updateCheck(currentVersion);
								if (newVersion > currentVersion) {
									log.warning("Version " + newVersionTitle + " has been released." + " You are currently running version " + currentVersionTitle);
									log.warning("Update at: http://dev.bukkit.org/bukkit-plugins/moneythief/");
								} else if (currentVersion > newVersion) {
									log.info("You are running an unsupported build!");
									log.info("The recommended version is " + newVersionTitle + ", and you are running " + currentVersionTitle);
									log.info("If the plugin has just recently updated, please ignore this message.");
								} else {
									log.info("Hooray! You are running the latest build!");
								}
							} catch (Exception e) {
								// ignore exceptions
							}
						}
					}
				}, 0, 430000);

			}

		});
        /**
         * Commands
         */
        getCommand("moneythief").setExecutor(new CommandHandler(this, pluginconfig));
    }

	@Override
	public void onDisable() {
		log.info("onDisable has been invoked!");
		getServer().getServicesManager().unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}



	public void reloadSongOne() {
		if (songOneData == null) {
			songOneData = new File(getDataFolder(), "songs/songOne.yml");
		}
		song1 = YamlConfiguration.loadConfiguration(songOneData);
		// Look for defaults in the jar
		Reader defConfigStream = new InputStreamReader(this.getResource("songs/songOne.yml"));
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			song1.setDefaults(defConfig);
		}
	}

	public FileConfiguration getSongOne() {
		if (song1 == null) {
			reloadSongOne();
		}
		return song1;
	}

	public void saveSongOne() {
		if (song1 == null || songOneData == null) {
			return;
		}
		try {
			getSongOne().save(songOneData);
		} catch (IOException ex) {
			getLogger().log(Level.SEVERE, "Could not save config to " + songOneData, ex);
		}
	}

	public void reloadSongTwo() {
		if (songTwoData == null) {
			songTwoData = new File(getDataFolder(), "songs/songTwo.yml");
		}
		song2 = YamlConfiguration.loadConfiguration(songTwoData);

		// Look for defaults in the jar
		Reader defConfigStream = new InputStreamReader(this.getResource("songs/songTwo.yml"));
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			song2.setDefaults(defConfig);
		}
	}

	public FileConfiguration getSongTwo() {
		if (song2 == null) {
			reloadSongTwo();
		}
		return song2;
	}

	public void saveSongTwo() {
		if (song2 == null || songTwoData == null) {
			return;
		}
		try {
			getSongTwo().save(songTwoData);
		} catch (IOException ex) {
			getLogger().log(Level.SEVERE, "Could not save config to " + songTwoData, ex);
		}
	}

	public void reloadSongThree() {
		if (songThreeData == null) {
			songThreeData = new File(getDataFolder(), "songs/songThree.yml");
		}
		song3 = YamlConfiguration.loadConfiguration(songThreeData);

		// Look for defaults in the jar
		Reader defConfigStream = new InputStreamReader(this.getResource("songs/songThree.yml"));
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			song3.setDefaults(defConfig);
		}
	}

	public FileConfiguration getSongThree() {
		if (song3 == null) {
			reloadSongThree();
		}
		return song3;
	}

	public void saveSongThree() {
		if (song3 == null || songThreeData == null) {
			return;
		}
		try {
			getSongThree().save(songThreeData);
		} catch (IOException ex) {
			getLogger().log(Level.SEVERE, "Could not save config to " + songThreeData, ex);
		}
	}

	public void saveDefaultSongOne() {
		if (songOneData == null) {
			songOneData = new File(getDataFolder(), "songs/songOne.yml");
		}
		if (!songOneData.exists()) {
			plugin.saveResource("songs/songOne.yml", false);
		}
	}
	public void saveDefaultSongTwo() {
		if (songTwoData == null) {
			songTwoData = new File(getDataFolder(), "songs/songTwo.yml");
		}
		if (!songTwoData.exists()) {
			plugin.saveResource("songs/songTwo.yml", false);
		}
	}

	public void saveDefaultSongThree() {
		if (songThreeData == null) {
			songThreeData = new File(getDataFolder(), "songs/songThree.yml");
		}
		if (!songThreeData.exists()) {
			plugin.saveResource("songs/songThree.yml", false);
		}
	}

	public double updateCheck(double currentVersion) {
		try {
			URL url = new URL("https://api.curseforge.com/servermods/files?projectids=89728");
			URLConnection conn = url.openConnection();
			conn.setReadTimeout(5000);
			conn.addRequestProperty("User-Agent", "MoneyThief Update Checker");
			conn.setDoOutput(true);
			final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			final String response = reader.readLine();
			final JSONArray array = (JSONArray) JSONValue.parse(response);

			if (array.size() == 0) {
				this.getLogger().warning("No files found, or Feed URL is bad.");
				return currentVersion;
			}
			// Pull the last version from the JSON
			newVersionTitle = ((String) ((JSONObject) array.get(array.size() - 1)).get("name")).replace("MoneyThief-", "").trim();
			return Double.valueOf(newVersionTitle.replaceFirst("\\.", "").trim());
		} catch (Exception e) {
			log.info("There was an issue attempting to check for the latest version.");
		}
		return currentVersion;
	}


}