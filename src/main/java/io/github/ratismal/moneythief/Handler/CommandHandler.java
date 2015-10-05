package io.github.ratismal.moneythief.handler;

import io.github.ratismal.moneythief.MoneyThief;
import io.github.ratismal.moneythief.config.Config;
import io.github.ratismal.moneythief.util.ProcessMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Created by Ratismal on 2015-10-05.
 */

public class CommandHandler implements CommandExecutor {

    private MoneyThief plugin;
    private Config config;

    public CommandHandler(MoneyThief plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    /**
     * Command executor
     *
     * @param sender Player who sent command
     * @param cmd    Command that was sent
     * @param label  Command alias that was used
     * @param args   Arguments that followed command
     * @return True if successful
     */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if ((cmd.getName().equalsIgnoreCase("moneythief")) && ((args.length == 0) || ((args.length == 1)
                && (args[0] == "help"))) && (sender.hasPermission("moneythief"))) {
            sender.sendMessage(ChatColor.GOLD + "|=======MoneyThief=======|");
            sender.sendMessage(ChatColor.DARK_PURPLE + "/moneythief" + ChatColor.GOLD + " - Display this menu");
            sender.sendMessage(ChatColor.DARK_PURPLE + "/moneythief reloadconfig" + ChatColor.GOLD + " - Reload configs");
            sender.sendMessage(ChatColor.DARK_PURPLE + "/moneythief worth" + ChatColor.GOLD + " - Display the worth of lives");
            sender.sendMessage(ChatColor.DARK_PURPLE + "/moneythief v" + ChatColor.GOLD + " - Displays plugin version");
            return true;
        }
        else if ((sender.hasPermission("moneythief") == false)) {
            noPerms(sender);
            return true;
        }
        if ((cmd.getName().equalsIgnoreCase("moneythief")) && (args[0].equalsIgnoreCase("v"))) {
            sender.sendMessage(ChatColor.GOLD + "MoneyThief" + ChatColor.DARK_PURPLE + " is running on version " + plugin.getDescription().getVersion());
            return true;
        }

        if ((cmd.getName().equalsIgnoreCase("moneythief")) && (args[0].equalsIgnoreCase("reloadconfig"))
                && (args.length == 1) && (sender.hasPermission("moneythief.reloadConfig"))) {
            reloadConfig();
            sender.sendMessage(ChatColor.GOLD + "Configs reloaded!");
            return true;
        }
        else if ((sender.hasPermission("moneythief.reloadConfig") == false)) {
            noPerms(sender);
            return true;
        }
        if ((cmd.getName().equalsIgnoreCase("moneythief")) && (args[0].equalsIgnoreCase("worth"))
                && (args.length == 1) && (sender.hasPermission("moneythief.worth"))) {
            for (String key : Config.Mobs.getMobs().keySet()){
                List<Double> list = Config.Mobs.getMobs().get(key);
                sender.sendMessage(key + ": " + list.get(0) + " - " + list.get(1));
            }
            return true;
        }
        else if ((sender.hasPermission("moneythief.worth") == false)) {
            noPerms(sender);
            return true;
        }
        return false;
    }

    public String searchConfig(String mob) {
        String worth = "" + Config.Mobs.getMobs().get(mob);
        return worth;
    }

    public void noPerms(CommandSender sender) {
        String noPerms = Config.Message.getNoPerms();
        noPerms = ProcessMessage.processGeneral(noPerms);
        sender.sendMessage(noPerms);
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        plugin.reloadSongOne();
        plugin.reloadSongTwo();
        plugin.reloadSongThree();
        plugin.getConfig();
        plugin.getSongOne();
        plugin.getSongTwo();
        plugin.getSongThree();

        config.reload(plugin.getConfig());
    }

}
