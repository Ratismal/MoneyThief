package io.github.ratismal.moneythief;

//import java.util.logging.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKillerListener implements Listener {

	//private Logger log = Logger.getLogger("Minecraft");
	Economy econ;
	MoneyThief plugin;
	FanfarePlayer music;
	FileConfiguration config;
	public PlayerKillerListener (MoneyThief instance) {
		plugin = instance;
		econ = instance.econ;
	}

	Player killer = null;
	Player killed = null;


	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		music = new FanfarePlayer(MoneyThief.plugin);
		config = MoneyThief.plugin.getConfig();
		if ((event.getEntity().getKiller() instanceof Player) && (event.getEntity().getKiller().hasPermission("moneythief.PVP"))) {
			if ((event.getEntity().hasPermission("moneythief.bypassPVP"))) {
				return;
			}
			

			double gained = config.getDouble("gained");
			double lost = config.getDouble("lost");
			String toKiller = config.getString("pk.killerone");
			String toVictimOne = config.getString("pk.victimone");
			String toVictimTwo = config.getString("pk.victimtwo");
			String toKillerZero = config.getString("pk.killerzero");
			String toVictimZero = config.getString("pk.victimzero");

			Player killed = event.getEntity();
			Player killer = event.getEntity().getKiller();



			double balKilled = econ.getBalance(killed);
			double balKiller = econ.getBalance(killer);

			if (balKilled > 0) {
				double taken = balKilled * (gained / 100);
				balKilled = balKilled - taken;
				double moneyLost = taken * (lost / 100);
				double moneyGiven = taken - moneyLost;
				balKiller = balKiller + moneyGiven;

				/*
			killed.sendMessage("You were killed and lost money.");
			killer.sendMessage("You killed someone and got money.");
				 */
				/*
				Bukkit.broadcastMessage("" + moneyGiven);
				Bukkit.broadcastMessage("" + taken);
				Bukkit.broadcastMessage("" + moneyLost);
				 */

				moneyGiven = Math.round(moneyGiven * 100);
				taken = Math.round(taken * 100);
				moneyLost = Math.round(moneyLost * 100);
				moneyGiven = moneyGiven / 100;
				taken = taken / 100;
				moneyLost = moneyLost / 100;

				/*
				Bukkit.broadcastMessage("" + moneyGiven);
				Bukkit.broadcastMessage("" + taken);
				Bukkit.broadcastMessage("" + moneyLost);
				 */

				String killedName = killed.getDisplayName();
				String killerName = killer.getDisplayName();

				/*
			log.info(toKiller);
			log.info(toVictimOne);
			log.info(toVictimTwo);
				 */

				int majorGiven = (int) moneyGiven;
				int minorGiven = (int) ((moneyGiven - majorGiven) * 100);
				int majorTaken = (int) taken;
				int minorTaken = (int) ((taken - majorTaken) * 100);
				int majorLost = (int) moneyLost;
				int minorLost = (int) ((moneyLost - majorLost) * 100);

				toKiller = toKiller.replaceAll("%VICTIM", killedName);
				toKiller = toKiller.replaceAll("%MONEYGAINED", Double.toString(moneyGiven));
				toVictimTwo = toVictimTwo.replaceAll("%MONEYTAKEN", Double.toString(taken));
				toVictimTwo = toVictimTwo.replaceAll("%MONEYLOST", Double.toString(moneyLost));
				toVictimOne = toVictimOne.replaceAll("%KILLER", killerName);
				toKiller = toKiller.replaceAll("%MAJOR", Integer.toString(majorGiven));
				toKiller = toKiller.replaceAll("%MINOR", Integer.toString(minorGiven));
				toVictimTwo = toVictimTwo.replaceAll("%MAJORTAKEN", Integer.toString(majorTaken));
				toVictimTwo = toVictimTwo.replaceAll("%MINORTAKEN", Integer.toString(minorTaken));
				toVictimTwo = toVictimTwo.replaceAll("%MAJORLOST", Integer.toString(majorLost));
				toVictimTwo = toVictimTwo.replaceAll("%MINORLOST", Integer.toString(minorLost));
				toKiller = ChatColor.translateAlternateColorCodes('&', toKiller);
				toVictimOne = ChatColor.translateAlternateColorCodes('&', toVictimOne);
				toVictimTwo = ChatColor.translateAlternateColorCodes('&', toVictimTwo);
				if (!(toVictimOne.equalsIgnoreCase("none"))) {
					killed.sendMessage(toVictimOne); //Message sent to victim upon
				}
				if (!(toVictimTwo.equalsIgnoreCase("none"))) {
					killed.sendMessage(toVictimTwo); //death
				}
				if (!(toKiller.equalsIgnoreCase("none"))) {
					killer.sendMessage(toKiller);//message sent to killer
				}

				econ.depositPlayer(killer, moneyGiven);
				econ.withdrawPlayer(killed, taken);

				if (config.getBoolean("enable-logging", true)) {
					try {

						Calendar cal = Calendar.getInstance();
						cal.getTime();
						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
						String content = "[" + sdf.format(cal.getTime()) + "] " + killed.getName() + " was killed by " + killer.getName();

						File file = new File(MoneyThief.plugin.getDataFolder(), "PlayerKills.log");

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

				music.songTwo(killer);
				music.songThree(killed);

			}
			else {

				String killedName = killed.getDisplayName();
				String killerName = killer.getDisplayName();

				toKillerZero = toKillerZero.replaceAll("%VICTIM", killedName);
				toVictimZero = toVictimZero.replaceAll("%KILLER", killerName);
				toKillerZero = ChatColor.translateAlternateColorCodes('&', toKillerZero);
				toVictimZero = ChatColor.translateAlternateColorCodes('&', toVictimZero);
				if (!(toVictimZero.equalsIgnoreCase("none"))) {
					killed.sendMessage(toVictimZero);
				}
				if (!(toKillerZero.equalsIgnoreCase("none"))) {
					killer.sendMessage(toKillerZero);
				}

				if (config.getBoolean("enable-logging", true)) {
					try {

						Calendar cal = Calendar.getInstance();
						cal.getTime();
						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
						String content = "[" + sdf.format(cal.getTime()) + "] " + killed.getName() + " was killed by " + killer.getName();

						File file = new File(MoneyThief.plugin.getDataFolder(), "PlayerKills.log");

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
		else if (!(event.getEntity().getKiller() instanceof Player)) {
			if ((event.getEntity().hasPermission("moneythief.bypassPVE"))) {
				return;
			}
			//System.out.println("Player killed by an entity! UH OH!");
			Entity killer = getCausedEntity(event);
			if (killer == null) return;
			
			EntityType killerType = killer.getType();
			
			Player killed = event.getEntity();
			String entity = "" + killerType;
			double lost = config.getDouble("lostpve");
			String firstLetter = entity.substring(0,1).toLowerCase();
			if ((firstLetter).equals("a") || (firstLetter).equals("e") || 
					(firstLetter).equals("i") || (firstLetter).equals("o")) {
				entity = "an " + entity;
			}
			else {
				entity = "a " + entity;
			}
			entity = entity.toLowerCase();

			//String killedName = killed.getDisplayName();
			//String killerName = killer.getDisplayName();

			
			
			if (econ.getBalance(killed) > 0){
				
				double balKilled = econ.getBalance(killed);
				double moneyLost = balKilled * (lost / 100);
				moneyLost = Math.round(moneyLost * 100);
				moneyLost = moneyLost / 100;
				int major = (int) moneyLost;
				int minor = (int) ((moneyLost - major) * 100);
				//System.out.println(major + " " + minor);
				
				String toVictim = config.getString("md.victim");

				toVictim = toVictim.replaceAll("%MOBNAME", entity);
				toVictim = toVictim.replaceAll("%MONEYLOST", Double.toString(moneyLost));
				toVictim = toVictim.replaceAll("%MAJOR", Integer.toString(major));
				toVictim = toVictim.replaceAll("%MINOR", Integer.toString(minor));
				toVictim = ChatColor.translateAlternateColorCodes('&', toVictim);

				if (!(toVictim.equalsIgnoreCase("none"))) {
					killed.sendMessage(toVictim); //Message sent to victim upon
				}
				//System.out.println("Withdrawing " + moneyLost);
				econ.withdrawPlayer(killed, moneyLost);	
				//econ.
				if (config.getBoolean("enable-logging", true)) {
					try {

						Calendar cal = Calendar.getInstance();
						cal.getTime();
						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
						String content = "[" + sdf.format(cal.getTime()) + "] " + killed.getName() + " was killed by " + entity;

						File file = new File(MoneyThief.plugin.getDataFolder(), "PlayerKills.log");

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
				music.songThree(killed);
			}
			else {

				//String killedName = killed.getDisplayName();
				String toVictimZero = config.getString("md.victimzero");
				//killer = event.getEntity().getKiller().getType();
				//killed = event.getEntity();
				//entity = "" + killer;

				firstLetter = entity.substring(0,1).toLowerCase();
				if ((firstLetter).equals("a") || (firstLetter).equals("e") || 
						(firstLetter).equals("i") || (firstLetter).equals("o")) {
					entity = "an " + entity;
				}
				else {
					entity = "a " + entity;
				}

				toVictimZero = toVictimZero.replaceAll("%MOBNAME", entity);
				toVictimZero = ChatColor.translateAlternateColorCodes('&', toVictimZero);
				if (!(toVictimZero.equalsIgnoreCase("none"))) {
					killed.sendMessage(toVictimZero);
				}
				if (config.getBoolean("enable-logging", true)) {
					try {

						Calendar cal = Calendar.getInstance();
						cal.getTime();
						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
						String content = "[" + sdf.format(cal.getTime()) + "] " + killed.getName() + " was killed by " + entity;

						File file = new File(MoneyThief.plugin.getDataFolder(), "PlayerKills.log");

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
	public static Entity getCausedEntity(PlayerDeathEvent event) {
	    EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
	    if (damageEvent != null && !damageEvent.isCancelled() && (damageEvent instanceof EntityDamageByEntityEvent)) {
	        EntityDamageByEntityEvent entityDamageEvent = (EntityDamageByEntityEvent) damageEvent;
	        Entity damager = entityDamageEvent.getDamager();
	        
	        return damager;
	    }
	    return null;
	}
}