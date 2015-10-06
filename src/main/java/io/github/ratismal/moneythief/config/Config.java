package io.github.ratismal.moneythief.config;

import io.github.ratismal.moneythief.MoneyThief;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Ratismal on 2015-10-05.
 */

public class Config {

    private FileConfiguration config;

    /**
     * Configs
     */

    /**
     * Metrics
     */
    public static final class Metrics {
        public static boolean isMetrics() {
            return metrics;
        }

        private static boolean metrics;
    }

    /**
     * General
     */
    public static final class General {
        public static boolean isCheckUpdate() {
            return checkUpdate;
        }

        public static boolean isNotifyMissingMob() {
            return notifyMissingMob;
        }

        public static boolean isLogKills() {
            return logKills;
        }

        private static boolean checkUpdate;
        private static boolean notifyMissingMob;
        private static boolean logKills;
    }

    /**
     * PVP
     */
    public static final class PVP {
        public static double getGained() {
            return gained;
        }

        public static double getLost() {
            return lost;
        }

        private static double gained;
        private static double lost;
    }

    /**
     * PVE
     */
    public static final class PVE {
        public static double getLost() {
            return lost;
        }

        public static boolean isArtificialSpawn() {
            return artificialSpawn;
        }

        private static double lost;
        private static boolean artificialSpawn;
    }

    /**
     * Messages
     */
    public static final class Message {
        public static String getNoPerms() {
            return noPerms;
        }

        public static String getPvpKiller() {
            return pvpKiller;
        }

        public static String getPvpVictim() {
            return pvpVictim;
        }

        public static String getPvpKillerZero() {
            return pvpKillerZero;
        }

        public static String getPvpVictimZero() {
            return pvpVictimZero;
        }

        public static String getPvpDeathMessage() {
            return pvpDeathMessage;
        }

        public static String getPveKiller() {
            return pveKiller;
        }

        public static String getPveVictimMob() {
            return pveVictimMob;
        }

        public static String getPveVictimMobZero() {
            return pveVictimMobZero;
        }

        public static String getPveVictimEnv() {
            return pveVictimEnv;
        }

        public static String getPveVictimEnvZero() {
            return pveVictimEnvZero;
        }

        public static String getPveGenericCause() {
            return pveGenericCause;
        }

        public static String getPveDeathMessage() {
            return pveDeathMessage;
        }

        //  - General
        private static String noPerms;

        //  - PVP
        private static String pvpKiller;
        private static String pvpVictim;
        private static String pvpKillerZero;
        private static String pvpVictimZero;
        private static String pvpDeathMessage;

        //  - PVE
        private static String pveKiller;
        private static String pveVictimMob;
        private static String pveVictimMobZero;
        private static String pveVictimEnv;
        private static String pveVictimEnvZero;
        private static String pveGenericCause;
        private static String pveDeathMessage;
    }

    /**
     * Mobs
     */
    public static final class Mobs {
        public static HashMap<String, List<Double>> getMobs() {
            return mobs;
        }

        private static HashMap<String, List<Double>> mobs = new HashMap<String, List<Double>>();
    }

    /**
     * Groups
     */
    public static final class Groups {

        public static HashMap<String, List<Double>> getGroups() {
            return groups;
        }

        private static HashMap<String, List<Double>> groups = new HashMap<String, List<Double>>();
    }

    /**
     * Config constructor
     *
     * @param config Config
     */
    public Config(FileConfiguration config) {
        this.config = config;
        reload(this.config);
    }

    /**
     * Reloads the config
     *
     * @param config
     */
    public void reload(FileConfiguration config) {
        //Metrics
        Metrics.metrics = config.getBoolean("metrics");

        //General
        General.checkUpdate = config.getBoolean("update-check");
        General.notifyMissingMob = config.getBoolean("notify-missing-mob");
        General.logKills = config.getBoolean("enable-logging");

        //PVP
        PVP.gained = config.getDouble("pvp.percent-taken");
        PVP.lost = config.getDouble("pvp.percent-lost");

        //PVE
        PVE.lost = config.getDouble("pve.percent-lost");
        PVE.artificialSpawn = config.getBoolean("pve.artificial-spawn");

        //Messages
        //  - General
        Message.noPerms = config.getString("message.general.no-perms");
        //  - PVP
        Message.pvpKiller = config.getString("message.pvp.killer");
        Message.pvpKillerZero = config.getString("message.pvp.killer-zero");
        Message.pvpVictim = config.getString("message.pvp.victim");
        Message.pvpVictimZero = config.getString("message.pvp.victim-zero");
        Message.pvpDeathMessage = config.getString("message.pvp.death-message");
        //   - PVE
        Message.pveKiller = config.getString("message.pve.killer");
        Message.pveVictimMob = config.getString("message.pve.victim-mob");
        Message.pveVictimMobZero = config.getString("message.pve.victim-mob-zero");
        Message.pveVictimEnv = config.getString("message.pve.victim-env");
        Message.pveVictimEnvZero = config.getString("message.pve.victim-env-zero");
        Message.pveGenericCause = config.getString("message.pve.generic-cause");
        Message.pveDeathMessage = config.getString("message.pve.death-message");

        //Mobs
        ConfigurationSection mobSection = config.getConfigurationSection("mobs");
        HashMap<String, List<Double>> temp = new HashMap<String, List<Double>>();
        if (mobSection != null) {
            for (String name : mobSection.getKeys(false)) {
                List<Double> values = mobSection.getDoubleList(name);
                temp.put(name, values);
            }
        }
        Mobs.mobs = (HashMap<String, List<Double>>) temp.clone();
        temp.clear();

        ConfigurationSection groupSection = config.getConfigurationSection("groups");
        if (groupSection != null) {
            for (String name : groupSection.getKeys(false)) {
                List<Double> values = groupSection.getDoubleList(name);
                temp.put(name, values);
            }
        }
        Groups.groups = (HashMap<String, List<Double>>) temp.clone();
        temp.clear();

    }


}