package io.github.ratismal.moneythief;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.ChatColor;

public class EntityKillerListener implements Listener {

	MoneyThief plugin;
	Economy econ;

	EntityKillerListener(MoneyThief instance) {
		plugin = instance;
		econ = instance.econ;
		//music = instance2;
	}

	FanfarePlayer music;

	public static HashMap<Integer, Boolean> spawnedNotNatural = new HashMap<>();


	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {

		if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
			spawnedNotNatural.put(event.getEntity().getEntityId(), true);
		}

	}


	FileConfiguration config;

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		config = MoneyThief.plugin.getConfig();
		//spawnedNotNatural.containsKey()
		if (config.getBoolean("artificial-spawn", true) || !spawnedNotNatural.containsKey(event.getEntity().getEntityId())) {
			//config.getBoolean("artificial-spawn", true) ||
			if ((Bukkit.getOnlinePlayers().contains(event.getEntity().getKiller())) && (event.getEntity().getKiller().hasPermission("moneythief.PVE"))) {
				if (!(event.getEntity() instanceof Player)) {
					music = new FanfarePlayer(MoneyThief.plugin);

					EntityType killed = event.getEntityType();
					String entity = "" + killed;
					final Player killer = event.getEntity().getKiller();

					String worth = searchConfig(entity);
					if (!worth.equals("null")) {
						double money = Double.parseDouble(worth);
						econ.depositPlayer(killer, money);

						if (!(config.getString("mk.killer")).equals("none")) {
							String prefix;
							String firstLetter = entity.substring(0, 1).toLowerCase();
							if ((firstLetter).equals("a") || (firstLetter).equals("e") ||
									(firstLetter).equals("i") || (firstLetter).equals("o")) {
								prefix = "an";
							} else {
								prefix = "a";
							}

							entity = entity.toLowerCase();
							money = Math.round(money * 100);
							money = money / 100;
							int major = (int) money;
							int minor = (int) ((money - major) * 100);
							String tokiller = config.getString("mk.killer");
							entity = entity.replaceAll("_", " ");
							tokiller = tokiller.replaceAll("%MONEYGAINED", Double.toString(money));
							tokiller = tokiller.replaceAll("%MOBNAME", entity);
							tokiller = tokiller.replaceAll("%MAJOR", Integer.toString(major));
							tokiller = tokiller.replaceAll("%MINOR", Integer.toString(minor));
							tokiller = tokiller.replaceAll("%A", prefix);

							tokiller = ChatColor.translateAlternateColorCodes('&', tokiller);

							killer.sendMessage(tokiller);

							music.songOne(killer);



							if (config.getBoolean("enable-logging", true)) {
								try {

									Calendar cal = Calendar.getInstance();
									cal.getTime();
									SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
									String content = "[" + sdf.format(cal.getTime()) + "] " + entity + " was killed by " + killer.getName();

									File file = new File(MoneyThief.plugin.getDataFolder(), "EntityKills.log");

									// if file doesn't exists, then create it
									if (!file.exists()) {
										file.createNewFile();
									}

									FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
									BufferedWriter bw = new BufferedWriter(fw);
									PrintWriter out = new PrintWriter(bw);
									out.println(content);
									bw.close();

									//System.out.println("Done");

								} catch (IOException e) {
									e.printStackTrace();
								}
							}

						}
					}

				}
			}
		}

	}

	public String searchConfig(String mob) {
		String worth = "null";
		try {
			List<Double> mobWorth = plugin.getConfig().getDoubleList("mobs." + mob);
			Double low = mobWorth.get(0);
			Double high = mobWorth.get(1);
			Random r = new Random();
			worth = Double.toString((low + (high - low) * r.nextDouble()));
		} catch (NullPointerException e) {
			plugin.getLogger().warning("Mob " + mob + " does not exist in MoneyThief config, consider adding");
		}
		return worth;
	}

}
