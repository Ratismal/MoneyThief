package io.github.ratismal.moneythief;

import java.util.Map;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.ChatColor;

public final class EntityKillerListener implements Listener {

	Economy econ = MoneyThief.econ;	
	public Map<String, Object> mobValues = MoneyThief.mobValues;
	public Map<String, Object> configValues = MoneyThief.configValues;
	
	@EventHandler(priority = EventPriority.HIGH)
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
					if (!("" + configValues.get("mk.killer")).equals("none")) {
						String firstLetter = entity.substring(0,1).toLowerCase();
						if ((firstLetter).equals("a") || (firstLetter).equals("e") || 
								(firstLetter).equals("i") || (firstLetter).equals("o")) {
							entity = "an " + entity;
						}
						else {
							entity = "a " + entity;
						}
						money = Math.round(money * 100) / 100;
						String tokiller = "" + configValues.get("mk.killer");
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
