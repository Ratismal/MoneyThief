package io.github.ratismal.moneythief;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKillerListener implements Listener {
	
	private Economy econ;
	FileConfiguration config = MoneyThief.plugin.getConfig();
	public PlayerKillerListener (MoneyThief instance) {
		econ = instance.econ;
	}
	
	Player killer = null;
	Player killed = null;
	double gained = config.getDouble("gained");
	double lost = config.getDouble("lost");
	String toKiller = config.getString("pk.tokiller");
	String toVictimOne = config.getString("pk.tovictimone");
	String toVictimTwo = config.getString("pk.tovictimtwo");
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
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
			
			moneyGiven = Math.round(moneyGiven * 100) / 100;
			taken = Math.round(taken * 100) / 100;
			moneyLost = Math.round(moneyLost * 100) / 100;

			toKiller = toKiller.replaceAll("%VICTIM", killed.getDisplayName());
			toKiller = toKiller.replaceAll("%MONEYGAINED", Double.toString(moneyGiven));
			toVictimTwo = toVictimTwo.replaceAll("%MONEYTAKEN", Double.toString(taken));
			toVictimTwo = toVictimTwo.replaceAll("%MONEYLOST", Double.toString(moneyLost));
			toVictimOne = toVictimOne.replaceAll("%KILLER", killer.getDisplayName());
			toKiller = ChatColor.translateAlternateColorCodes('&', toKiller);
			toVictimOne = ChatColor.translateAlternateColorCodes('&', toVictimOne);
			toVictimTwo = ChatColor.translateAlternateColorCodes('&', toVictimTwo);
			
			killed.sendMessage(toVictimOne); //Message sent to victim upon
			killed.sendMessage(toVictimTwo); //death
			killer.sendMessage(toKiller);//message sent to killer
			
			
			econ.depositPlayer(killer, moneyGiven);
			econ.withdrawPlayer(killed, taken);
		}
		}
	}
	
}