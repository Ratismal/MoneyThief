package io.github.ratismal.moneythief;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.ratismal.moneythief.PlayerKillerListener;

public class MoneyThief extends JavaPlugin {
	
	public static MoneyThief plugin;
	private Logger log = Logger.getLogger("Minecraft");
    public Economy econ = null;
    public Map<String, Object> configValues = new HashMap<String, Object>();
    public Map<String, Object> mobValues = new HashMap<String, Object>();
    
	@Override
    public void onEnable() {
		
		
		getLogger().info("[MoneyThief] onEnable has been invoked!");
		plugin = this;
		
		PluginManager pm = this.getServer().getPluginManager();
		
		this.saveDefaultConfig();
		getConfig();
	    configValues = this.getConfig().getConfigurationSection("").getValues(true);
	    mobValues = this.getConfig().getConfigurationSection("mobs").getValues(true);

		if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		pm.registerEvents(new PlayerKillerListener(this), this);
		log.info("[MoneyThief] Player Listener Enabled");
		pm.registerEvents(new EntityKillerListener(this), this);
		log.info("[MoneyThief] Entity Listener Enabled");
		
	}
 
    @Override
    public void onDisable() {
    	getLogger().info("onDisable has been invoked!");
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

@Override
public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	
	if ((cmd.getName().equalsIgnoreCase("moneythief")) && ((args.length == 0) || ((args.length == 1) 
			&& (args[0] == "help"))) && (sender.hasPermission("moneythief"))) {
		sender.sendMessage(ChatColor.GOLD + "|=======MoneyThief=======|");
		sender.sendMessage(ChatColor.DARK_PURPLE + "/moneythief" + ChatColor.GOLD + " - Display this menu");
		sender.sendMessage(ChatColor.DARK_PURPLE + "/moneythief reloadconfig" + ChatColor.GOLD + " - Reload configs");
		sender.sendMessage(ChatColor.DARK_PURPLE + "/moneythief worth" + ChatColor.GOLD + " - Display the worth of lives");
		return true;
	}
	else if ((sender.hasPermission("moneythief") == false)) {
		sender.sendMessage(ChatColor.RED + "Bugger off, you don't have the right permissions!");
		return true;
	}
	
	if ((cmd.getName().equalsIgnoreCase("moneythief")) && (args[0].equalsIgnoreCase("reloadconfig")) 
			&& (args.length == 1) && (sender.hasPermission("moneythief.reloadConfig"))) { 
		this.reloadConfig();
		this.getConfig();
		configValues = this.getConfig().getConfigurationSection("").getValues(true);
		mobValues = this.getConfig().getConfigurationSection("mobs").getValues(true);
		sender.sendMessage("Configs reloaded!");
		
		return true;
	} 
	else if ((sender.hasPermission("moneythief.reloadConfig") == false)) {
		sender.sendMessage(ChatColor.RED + "Bugger off, you don't have the right permissions!");
		return true;
	}
	
	if ((cmd.getName().equalsIgnoreCase("moneythief")) && (args[0].equalsIgnoreCase("worth")) 
			&& (args.length == 1) && (sender.hasPermission("moneythief.worth"))) {
		// Get a set of the entries
	      @SuppressWarnings("rawtypes")
		Set set = mobValues.entrySet();
	      // Get an iterator
	      @SuppressWarnings("rawtypes")
		Iterator i = set.iterator();
	      // Display elements
	      while(i.hasNext()) {
	         @SuppressWarnings("rawtypes")
			Map.Entry me = (Map.Entry)i.next();
	         sender.sendMessage(me.getKey() + ": " + me.getValue());
	      }
		return true;
	} 
	else if ((sender.hasPermission("moneythief.worth") == false)) {
		sender.sendMessage(ChatColor.RED + "Bugger off, you don't have the right permissions!");
		return true;
	}
	return false; 
}



public String searchConfig(String mob) {
	String worth = "" + mobValues.get(mob);
	return worth;
}

}