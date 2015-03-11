package io.github.ratismal.moneythief;

//import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
		if ((event.getEntity().getKiller() instanceof Player) && (event.getEntity().getKiller().hasPermission("moneythief.PVP"))) {
			if ((event.getEntity().hasPermission("moneythief.bypassPVP"))) {
				return;
			}
			music = new FanfarePlayer(MoneyThief.plugin);
			config = MoneyThief.plugin.getConfig();

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

				toKiller = toKiller.replaceAll("%VICTIM", killedName);
				toKiller = toKiller.replaceAll("%MONEYGAINED", Double.toString(moneyGiven));
				toVictimTwo = toVictimTwo.replaceAll("%MONEYTAKEN", Double.toString(taken));
				toVictimTwo = toVictimTwo.replaceAll("%MONEYLOST", Double.toString(moneyLost));
				toVictimOne = toVictimOne.replaceAll("%KILLER", killerName);
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
			}
		}
	}


}