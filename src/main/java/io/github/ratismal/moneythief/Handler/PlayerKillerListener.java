package io.github.ratismal.moneythief.handler;

import io.github.ratismal.moneythief.config.Config;
import io.github.ratismal.moneythief.util.FanfarePlayer;
import io.github.ratismal.moneythief.MoneyThief;
import io.github.ratismal.moneythief.util.KillLogger;
import io.github.ratismal.moneythief.util.ProcessMessage;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKillerListener implements Listener {

    Economy econ;
    MoneyThief plugin;
    FanfarePlayer music;

    public PlayerKillerListener(MoneyThief instance) {
        plugin = instance;
        econ = instance.econ;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        this.music = new FanfarePlayer(this.plugin);
        Player killerEntity = event.getEntity().getKiller();
        /**
         * Event is PVP
         */
        if ((Bukkit.getOnlinePlayers().contains(killerEntity)) && (killerEntity.hasPermission("moneythief.PVP"))) {

            if ((event.getEntity().hasPermission("moneythief.bypassPVP"))) {
                return;
            }
            pvp(event);

            /**
             * Event is PVE
             */
        } else/* if (!(event.getEntity().getKiller() instanceof Player)) */ {
            if ((event.getEntity().hasPermission("moneythief.bypassPVE"))) {
                return;
            }
            double lost = Config.PVE.getLost();
            if (lost != 0) {
                if (wasKilledByMob(event)) {
                    pveMob(event, lost);
                } else {
                    pveEnv(event, lost);
                }
            }
        }
    }

    private void pvp(PlayerDeathEvent event) {

        double gained = Config.PVP.getGained();
        double lost = Config.PVP.getLost();


        if (gained != 0) {

            Player killed = event.getEntity();
            Player killer = event.getEntity().getKiller();


            double balKilled = econ.getBalance(killed);

            if (balKilled > 0) {
                double taken = balKilled * (gained / 100);
                double moneyLost = taken * (lost / 100);
                double moneyGiven = taken - moneyLost;

                moneyGiven = Math.round(moneyGiven * 100);
                taken = Math.round(taken * 100);
                moneyLost = Math.round(moneyLost * 100);
                moneyGiven = moneyGiven / 100;
                taken = taken / 100;
                moneyLost = moneyLost / 100;

                String killedName = killed.getDisplayName();
                String killerName = killer.getDisplayName();

                int majorGiven = (int) moneyGiven;
                int minorGiven = (int) ((moneyGiven - majorGiven) * 100);
                int majorTaken = (int) taken;
                int minorTaken = (int) ((taken - majorTaken) * 100);
                int majorLost = (int) moneyLost;
                int minorLost = (int) ((moneyLost - majorLost) * 100);

                String toKiller = ProcessMessage.processPVP(Config.Message.getPvpKiller(), killedName, moneyGiven, taken, moneyLost, killerName, majorGiven,
                        minorGiven, majorTaken, minorTaken, majorLost, minorLost);
                String toVictim = ProcessMessage.processPVP(Config.Message.getPvpVictim(), killedName, moneyGiven, taken, moneyLost, killerName, majorGiven,
                        minorGiven, majorTaken, minorTaken, majorLost, minorLost);
                String pvpMessage = ProcessMessage.processPVP(Config.Message.getPvpDeathMessage(), killedName, moneyGiven, taken, moneyLost, killerName, majorGiven,
                        minorGiven, majorTaken, minorTaken, majorLost, minorLost);

                if (!(toVictim.equalsIgnoreCase("none"))) {
                    killed.sendMessage(toVictim); //Message sent to victim upon
                }
                if (!(toKiller.equalsIgnoreCase("none"))) {
                    killer.sendMessage(toKiller);//message sent to killer
                }
                pvpMessage = pvpMessage.replaceAll("%KILLER", killerName);
                pvpMessage = pvpMessage.replaceAll("%VICTIM", killedName);
                pvpMessage = ChatColor.translateAlternateColorCodes('&', pvpMessage);
                if (!(pvpMessage.equalsIgnoreCase("none"))) {
                    if (pvpMessage.equalsIgnoreCase("disable")) {
                        event.setDeathMessage(null);
                    } else {
                        event.setDeathMessage(pvpMessage);
                    }
                }
                econ.depositPlayer(killer, moneyGiven);
                econ.withdrawPlayer(killed, taken);

                KillLogger.logPvp(killer, killed);

                music.songTwo(killer);
                music.songThree(killed);

            } else {

                String killedName = killed.getDisplayName();
                String killerName = killer.getDisplayName();

                String toKillerZero = ProcessMessage.processPVP(Config.Message.getPvpKillerZero(), killedName, killerName);
                String toVictimZero = ProcessMessage.processPVP(Config.Message.getPvpVictimZero(), killedName, killerName);
                toKillerZero = ChatColor.translateAlternateColorCodes('&', toKillerZero);
                toVictimZero = ChatColor.translateAlternateColorCodes('&', toVictimZero);
                if (!(toVictimZero.equalsIgnoreCase("none"))) {
                    killed.sendMessage(toVictimZero);
                }
                if (!(toKillerZero.equalsIgnoreCase("none"))) {
                    killer.sendMessage(toKillerZero);
                }

                KillLogger.logPvp(killer, killed);

            }
        }
    }

    public void pveMob(PlayerDeathEvent event, double lost) {
        Entity killer = getCausedEntity(event);
        if (killer == null) return;

        EntityType killerType = killer.getType();

        Player killed = event.getEntity();
        String entity = "" + killerType;

        String prefix;
        String firstLetter = entity.substring(0, 1).toLowerCase();
        if ((firstLetter).equals("a") || (firstLetter).equals("e") ||
                (firstLetter).equals("i") || (firstLetter).equals("o")) {
            prefix = "an";
        } else {
            prefix = "a";
        }
        entity = entity.toLowerCase();

        //String killedName = killed.getDisplayName();
        //String killerName = killer.getDisplayName();


        if (econ.getBalance(killed) > 0) {

            double balKilled = econ.getBalance(killed);
            double moneyLost = balKilled * (lost / 100);
            moneyLost = Math.round(moneyLost * 100);
            moneyLost = moneyLost / 100;
            int major = (int) moneyLost;
            int minor = (int) ((moneyLost - major) * 100);
            //System.out.println(major + " " + minor);

            String toVictim = ProcessMessage.processMobPVE(Config.Message.getPveVictimMob(), entity, moneyLost, major, minor, prefix);

            if (!(toVictim.equalsIgnoreCase("none"))) {
                killed.sendMessage(toVictim); //Message sent to victim upon
            }
            String pveMessage = ProcessMessage.processMobPVE(Config.Message.getPveDeathMessage(), entity, moneyLost, major, minor, prefix);
            if (!(pveMessage.equalsIgnoreCase("none"))) {
                if (pveMessage.equalsIgnoreCase("disable")) {
                    event.setDeathMessage(null);
                } else {
                    event.setDeathMessage(pveMessage);
                }
            }
            //System.out.println("Withdrawing " + moneyLost);
            econ.withdrawPlayer(killed, moneyLost);
            //econ.
            KillLogger.logPve(killed, entity, true);
            music.songThree(killed);
        } else {

            //String killedName = killed.getDisplayName();

            //killer = event.getEntity().getKiller().getType();
            //killed = event.getEntity();
            //entity = "" + killer;
            firstLetter = entity.substring(0, 1).toLowerCase();
            if ((firstLetter).equals("a") || (firstLetter).equals("e") ||
                    (firstLetter).equals("i") || (firstLetter).equals("o")) {
                prefix = "an";
            } else {
                prefix = "a";
            }
            String toVictimZero = ProcessMessage.processMobPVE(Config.Message.getPveVictimMobZero(), entity, prefix);
            if (!(toVictimZero.equalsIgnoreCase("none"))) {
                killed.sendMessage(toVictimZero);
            }
            KillLogger.logPve(killed, entity, true);
        }
    }

    public void pveEnv(PlayerDeathEvent event, double lost) {
        EntityDamageEvent.DamageCause damageCause = getCause(event);
        String cause;
        try {
            cause = damageCause.name();
            cause = cause.toLowerCase();
            cause = cause.replaceAll("_", " ");
        } catch (NullPointerException e) {
            cause = Config.Message.getPveGenericCause();
        }
        Player killed = event.getEntity();

        if (econ.getBalance(killed) > 0) {

            double balKilled = econ.getBalance(killed);
            double moneyLost = balKilled * (lost / 100);
            moneyLost = Math.round(moneyLost * 100);
            moneyLost = moneyLost / 100;
            int major = (int) moneyLost;
            int minor = (int) ((moneyLost - major) * 100);
            //System.out.println(major + " " + minor);

            String toVictim = ProcessMessage.processEnvPVE(Config.Message.getPveVictimEnv(), cause, moneyLost, major, minor);

            econ.withdrawPlayer(killed, moneyLost);
            //econ.
            if (!(toVictim.equalsIgnoreCase("none"))) {
                killed.sendMessage(toVictim); //Message sent to victim upon
            }
            KillLogger.logPve(killed, cause, true);
            music.songThree(killed);

        } else {
            String toVictim = ProcessMessage.processEnvPVE(Config.Message.getPveVictimEnv(), cause);

            if (!(toVictim.equalsIgnoreCase("none"))) {
                killed.sendMessage(toVictim); //Message sent to victim upon
            }

            KillLogger.logPve(killed, cause, true);
            music.songThree(killed);
        }
    }

    public static Entity getCausedEntity(PlayerDeathEvent event) {
        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
        if (damageEvent != null && !damageEvent.isCancelled() && (damageEvent instanceof EntityDamageByEntityEvent)) {
            EntityDamageByEntityEvent entityDamageEvent = (EntityDamageByEntityEvent) damageEvent;
            return entityDamageEvent.getDamager();
        }
        return null;
    }

    public static boolean wasKilledByMob(PlayerDeathEvent event) {
        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
        return damageEvent != null && !damageEvent.isCancelled() && (damageEvent instanceof EntityDamageByEntityEvent);
    }

    public static EntityDamageEvent.DamageCause getCause(PlayerDeathEvent event) {
        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
        if (damageEvent != null && !damageEvent.isCancelled()) {
            return damageEvent.getCause();
        }
        return null;
    }

}