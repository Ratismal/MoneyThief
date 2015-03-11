package io.github.ratismal.moneythief;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.ChatColor;

public class EntityKillerListener implements Listener {

	MoneyThief plugin;
	Economy econ;
	EntityKillerListener (MoneyThief instance) {
		plugin = instance;
		econ = instance.econ;
		//music = instance2;
	}
	FanfarePlayer music;


	FileConfiguration config;
	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if ((event.getEntity().getKiller() instanceof Player) && (event.getEntity().getKiller().hasPermission("moneythief.PVE"))) {
			if (!(event.getEntity() instanceof Player)) {
				music = new FanfarePlayer(MoneyThief.plugin);
				config = MoneyThief.plugin.getConfig();
				EntityType killed = event.getEntityType();
				String entity = "" + killed;
				final Player killer = event.getEntity().getKiller();

				String worth = searchConfig(entity);
				if (worth != null) {
					double money = Double.parseDouble(worth);
					econ.depositPlayer(killer, money);
					if (!(config.getString("mk.killer")).equals("none")) {
						String firstLetter = entity.substring(0,1).toLowerCase();
						if ((firstLetter).equals("a") || (firstLetter).equals("e") || 
								(firstLetter).equals("i") || (firstLetter).equals("o")) {
							entity = "an " + entity;
						}
						else {
							entity = "a " + entity;
						}
						money = Math.round(money * 100);
						money = money / 100;
						String tokiller = config.getString("mk.killer");
						entity = entity.replaceAll("_", " ");
						tokiller = tokiller.replaceAll("%MONEYGAINED", Double.toString(money));
						tokiller = tokiller.replaceAll("%MOBNAME", entity);

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

								System.out.println("Done");

							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}
				}

			}
		}
	}

	public String searchConfig(String mob) {

		List<Double> mobWorth = plugin.getConfig().getDoubleList("mobs." + mob);
		Double low = mobWorth.get(0);
		Double high = mobWorth.get(1);
		Random r = new Random();
		String worth = "" + (low + (high - low) * r.nextDouble());
		return worth;
	}

}
