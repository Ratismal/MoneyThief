package io.github.ratismal.moneythief;

import java.util.Map;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class EntityKillerListener implements Listener {

	MoneyThief plugin;
	Economy econ;
	private Map<String, Object> mobValues;
	EntityKillerListener (MoneyThief instance) {
		plugin = instance;
		econ = instance.econ;
		mobValues = instance.mobValues;
	}
	FileConfiguration config = MoneyThief.plugin.getConfig();
	private Logger log = Logger.getLogger("Minecraft");
	
	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
			if (!(event.getEntity() instanceof Player)) {
				EntityType killed = event.getEntityType();
				String entity = "" + killed;
				Player killer = event.getEntity().getKiller();
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
						money = Math.round(money * 100) / 100;
						String tokiller = config.getString("mk.killer");
						entity = entity.replaceAll("_", " ");
						tokiller = tokiller.replaceAll("%MONEYGAINED", Double.toString(money));
						tokiller = tokiller.replaceAll("%MOBNAME", entity);

						tokiller = ChatColor.translateAlternateColorCodes('&', tokiller);
						
						killer.sendMessage(tokiller);
					}
				}
				
			}
		}
	}
	
	public String searchConfig(String mob) {
		String worth = "" + mobValues.get(mob);
		return worth;
	}
	
}
