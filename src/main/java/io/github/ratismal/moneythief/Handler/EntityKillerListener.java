package io.github.ratismal.moneythief.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.github.ratismal.moneythief.config.Config;
import io.github.ratismal.moneythief.util.FanfarePlayer;
import io.github.ratismal.moneythief.MoneyThief;
import io.github.ratismal.moneythief.util.KillLogger;
import io.github.ratismal.moneythief.util.ProcessMessage;
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

    public EntityKillerListener(MoneyThief instance) {
        plugin = instance;
        econ = instance.econ;
        //music = instance2;
    }


    private static HashMap<Integer, Boolean> spawnedNotNatural = new HashMap<>();


    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            spawnedNotNatural.put(event.getEntity().getEntityId(), true);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        //spawnedNotNatural.containsKey()
        if (Config.PVE.isArtificialSpawn() || !spawnedNotNatural.containsKey(event.getEntity().getEntityId())) {
            //config.getBoolean("artificial-spawn", true) ||
            if ((Bukkit.getOnlinePlayers().contains(event.getEntity().getKiller())) && (event.getEntity().getKiller().hasPermission("moneythief.PVE"))) {
                if (!(event.getEntity() instanceof Player)) {
                    music = new FanfarePlayer(MoneyThief.plugin);

                    EntityType killed = event.getEntityType();
                    String entity = "" + killed;
                    final Player killer = event.getEntity().getKiller();

                    Double worth = searchConfig(entity);
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

                            String toKiller = ProcessMessage.processMobPVE(Config.Message.getPveKiller(), entity, money, major, minor, prefix);

                            killer.sendMessage(toKiller);

                            music.songOne(killer);

                            KillLogger.logPve(killer, entity, false);

                        }
                    }

                }
            }
        }
    }

    public double searchConfig(String mob) {
        Double worth = 0.0;
        try {
            List<Double> mobWorth = plugin.getConfig().getDoubleList("mobs." + mob);
            Double low = mobWorth.get(0);
            Double high = mobWorth.get(1);
            Random r = new Random();
            worth = low + (high - low) * r.nextDouble();
        } catch (Exception e) {
            if ( Config.General.isNotifyMissingMob())
                plugin.getLogger().warning("Mob " + mob + " does not exist in MoneyThief config, or was added incorrectly. Please revise!");
        }
        return worth;
    }
}
