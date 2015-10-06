package io.github.ratismal.moneythief.handler;

import io.github.ratismal.moneythief.MoneyThief;
import io.github.ratismal.moneythief.config.Config;
import io.github.ratismal.moneythief.util.MessageProcessor;
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

    /**
     * CommandHandler constructor
     *
     * @param plugin MoneyThief plugin
     * @param config config
     */
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
     * @return true if successful
     */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("moneythief")) {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "v":
                        version(sender);
                        break;
                    case "worth":
                        if (hasPerm(sender, "moneythief.worth")) {
                            if (args.length > 1) {
                                searchMobs(sender, args[1]);
                            } else {
                                worth(sender);
                            }
                        }
                        break;
                    case "group":
                        if (hasPerm(sender, "moneythief.group")) {
                            if (args.length > 1) {
                                searchGroups(sender, args[1]);
                            } else {
                                groups(sender);
                            }
                        }
                        break;
                    case "reloadconfig":
                        if (hasPerm(sender, "moneythief.reloadConfig")) {
                            reloadConfig(sender);
                        }
                        break;
                    default:
                        help(sender);
                        break;
                }
                return true;
            } else {
                help(sender);
                return true;
            }
        }
        return false;
    }

    /**
     * Check if sender has permissions to do command
     *
     * @param sender player who did command
     * @param perm   permission to check
     * @return true if they have permission
     */
    boolean hasPerm(CommandSender sender, String perm) {
        if (!sender.hasPermission(perm)) {
            noPerms(sender);
            return false;
        }
        return true;
    }

    /**
     * Sends help message to player
     *
     * @param sender player
     */
    void help(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "|=======MoneyThief=======|");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/moneythief" + ChatColor.GOLD + " - Display this menu");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/moneythief reloadconfig" + ChatColor.GOLD + " - Reload configs");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/moneythief worth [mob]" + ChatColor.GOLD + " - Display the worth of lives");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/moneythief group [group]" + ChatColor.GOLD + " - Displays group values");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/moneythief v" + ChatColor.GOLD + " - Displays plugin version");
    }

    /**
     * Tells player plugin version
     *
     * @param sender player
     */
    void version(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "MoneyThief" + ChatColor.LIGHT_PURPLE + " is running on version " + plugin.getDescription().getVersion());
    }

    /**
     * Tells player a list of all mobs in config, and how much they're worth
     *
     * @param sender player
     */
    void worth(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "[MoneyThief] " + ChatColor.LIGHT_PURPLE + "Mob Values:");
        for (String key : Config.Mobs.getMobs().keySet()) {
            List<Double> list = Config.Mobs.getMobs().get(key);
            String message = key;
            for (int i = 0; i <= list.size() - 1; i++) {
                if (i == 0) {
                    message = message + ": ";
                } else {
                    message = message + " - ";
                }
                message = message + list.get(i);
            }
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "  " + message);
        }
    }

    /**
     * Tells the player a list of all groups in config, and their values
     *
     * @param sender player
     */
    void groups(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "[MoneyThief] " + ChatColor.LIGHT_PURPLE + "Group Values:");
        for (String key : Config.Groups.getGroups().keySet()) {
            List<Double> list = Config.Groups.getGroups().get(key);
            String message = key;
            for (int i = 0; i <= list.size() - 1; i++) {
                if (i == 0) {
                    message = message + ": ";
                } else {
                    message = message + " - ";
                }
                message = message + list.get(i);
            }
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "  " + message);
        }
    }

    /**
     * Tell the player how much a specific mob is worth
     *
     * @param sender player
     * @param mob    mob
     */
    public void searchMobs(CommandSender sender, String mob) {
        String msg;

        if (Config.Mobs.getMobs().containsKey(mob)) {
            sender.sendMessage(ChatColor.GOLD + "[MoneyThief] " + ChatColor.LIGHT_PURPLE + "Mob Values [" + mob + "]:");
            msg = ChatColor.LIGHT_PURPLE + "  " + mob;
            List<Double> list = Config.Mobs.getMobs().get(mob);
            for (int i = 0; i <= list.size() - 1; i++) {
                if (i == 0) {
                    msg = msg + ": ";
                } else {
                    msg = msg + " - ";
                }
                msg = msg + list.get(i);
                //sender.sendMessage(key + ": " + list.get(0) + " - " + list.get(1));
            }
        } else {
            msg = ChatColor.GOLD + "[MoneyThief] " + ChatColor.LIGHT_PURPLE + "Mob '" + mob + "' does not exist in config!";
        }
        sender.sendMessage(msg);
    }

    /**
     * Tells a player the values for a specific group
     *
     * @param sender player
     * @param group  group
     */
    public void searchGroups(CommandSender sender, String group) {
        String msg;

        if (Config.Groups.getGroups().containsKey(group)) {
            sender.sendMessage(ChatColor.GOLD + "[MoneyThief] " + ChatColor.LIGHT_PURPLE + "Group Values [" + group + "]:");

            msg = ChatColor.LIGHT_PURPLE + "  " + group;
            List<Double> list = Config.Groups.getGroups().get(group);
            for (int i = 0; i <= list.size() - 1; i++) {
                if (i == 0) {
                    msg = msg + ": ";
                } else {
                    msg = msg + " - ";
                }
                msg = msg + list.get(i);
                //sender.sendMessage(key + ": " + list.get(0) + " - " + list.get(1));
            }
        } else {
            msg = ChatColor.GOLD + "[MoneyThief] " + ChatColor.LIGHT_PURPLE + "Group '" + group + "' does not exist in config!";

        }
        sender.sendMessage(msg);
    }

    /**
     * Tells the player they don't have permissions to do a command
     *
     * @param sender player
     */
    public void noPerms(CommandSender sender) {
        String noPerms = Config.Message.getNoPerms();
        noPerms = MessageProcessor.processGeneral(noPerms);
        sender.sendMessage(noPerms);
    }

    /**
     * Reloads the configs, and tells the player it has been reloaded
     *
     * @param sender player
     */
    public void reloadConfig(CommandSender sender) {
        plugin.reloadConfig();
        plugin.reloadSongs();
        plugin.getConfig();
        plugin.getSongOne();
        plugin.getSongTwo();
        plugin.getSongThree();

        config.reload(plugin.getConfig());
        sender.sendMessage(ChatColor.GOLD + "Configs reloaded!");
    }

}
