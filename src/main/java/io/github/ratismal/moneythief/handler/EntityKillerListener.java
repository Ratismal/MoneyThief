package io.github.ratismal.moneythief.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.github.ratismal.moneythief.config.Config;
import io.github.ratismal.moneythief.util.FanfarePlayer;
import io.github.ratismal.moneythief.MoneyThief;
import io.github.ratismal.moneythief.util.KillLogger;
import io.github.ratismal.moneythief.util.MessageProcessor;
import io.github.ratismal.moneythief.util.PermissionChecker;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityKillerListener implements Listener {

    private MoneyThief plugin;
    private Economy econ;
    private FanfarePlayer music;

    /**
     * EntityKillerListener constructor
     *
     * @param instance MoneyThief plugin
     */
    public EntityKillerListener(MoneyThief instance) {
        plugin = instance;
        econ = instance.econ;
    }

    //A list of mobs that have been artificially spawned
    private static HashMap<Integer, Boolean> spawnedNotNatural = new HashMap<>();

    /**
     * Determines if a mob has been spawned in artificially,
     * and if it has been put it in the spawnedNotNatural list
     *
     * @param event CreatureSpawnEvent
     */
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!Config.PVE.isArtificialSpawn()) {
            if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
                spawnedNotNatural.put(event.getEntity().getEntityId(), true);
            }
        }
    }

    /**
     * Reward player for killing a mob
     *
     * @param event EntityDeathEvent
     */
    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (Config.PVE.isArtificialSpawn() || !spawnedNotNatural.containsKey(event.getEntity().getEntityId())) {
            if ((Bukkit.getOnlinePlayers().contains(event.getEntity().getKiller())) && (event.getEntity().getKiller().hasPermission("moneythief.PVE"))) {
                if (!(event.getEntity() instanceof Player)) {
                    music = new FanfarePlayer(MoneyThief.plugin);

                    EntityType killed = event.getEntityType();
                    String entity = "" + killed;
                    final Player killer = event.getEntity().getKiller();

                    Double worth = searchConfig(entity);
                    for (String group : Config.Groups.getGroups().keySet()) {
                        if (PermissionChecker.hasPermission(killer, "moneythief.group." + group)) {
                            worth = worth * Config.Groups.getGroups().get(group).get(0);
                        }
                    }
                    if (worth != 0) {
                        double money = worth;
                        econ.depositPlayer(killer, money);

                        if (!(Config.Message.getPveKiller()).equals("none")) {
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
                            entity = entity.replaceAll("_", " ");

                            String toKiller = MessageProcessor.processMobPVE(Config.Message.getPveKiller(), entity, money, major, minor, prefix);

                            killer.sendMessage(toKiller);

                            music.songOne(killer);

                            KillLogger.logPve(killer, entity, false);

                        }
                    }

                }
            }
        }
    }

    /**
     * Searches Config.Mobs.mobs for a mob
     *
     * @param mob mob to search for
     * @return the mob's worth
     */
    public double searchConfig(String mob) {
        Double worth = 0.0;
        HashMap<String, List<Double>> list = Config.Mobs.getMobs();
        try {
            if (list.containsKey(mob)) {
                List<Double> mobWorth = Config.Mobs.getMobs().get(mob);
                Double low = mobWorth.get(0);
                Double high;
                if (mobWorth.size() >= 2) {
                    high = mobWorth.get(1);
                } else {
                    high = low;
                }
                Random r = new Random();
                worth = low + (high - low) * r.nextDouble();
            } else if (Config.Mobs.getMobs().containsKey("DEFAULT")) {
                List<Double> mobWorth = Config.Mobs.getMobs().get("DEFAULT");
                Double low = mobWorth.get(0);
                Double high = mobWorth.get(1);
                Random r = new Random();
                worth = low + (high - low) * r.nextDouble();
                if (Config.General.isNotifyMissingMob()) {
                    plugin.getLogger().info("Using default values for mob " + mob);
                }
            } else if (Config.General.isNotifyMissingMob()) {
                plugin.getLogger().warning("Mob " + mob + " does not exist in MoneyThief config, or was added incorrectly. Please revise!");
            }
        } catch (Exception e) {
            if (Config.General.isNotifyMissingMob()) {
                plugin.getLogger().warning("Mob " + mob + " does not exist in MoneyThief config, or was added incorrectly. Please revise!");
            }
        }

        return worth;
    }
}
