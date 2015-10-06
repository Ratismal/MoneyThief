package io.github.ratismal.moneythief.util;

import io.github.ratismal.moneythief.MoneyThief;
import io.github.ratismal.moneythief.config.Config;
import org.bukkit.entity.Player;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Ratismal on 2015-10-05.
 */

public class KillLogger {

    /**
     * Log a PVP event
     *
     * @param killer Player who killed
     * @param killed Player killed
     */
    public static void logPvp(Player killer, Player killed) {
        if (Config.General.isLogKills()) {
            try {
                Calendar cal = Calendar.getInstance();
                cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String content = "[" + sdf.format(cal.getTime()) + "] " + killed.getName() + " was killed by " + killer.getName();

                File file = new File(MoneyThief.plugin.getDataFolder(), "PlayerKills.log");

                // if file doesn't exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw);
                out.println(content);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param player          The player
     * @param entity          The entity/cause
     * @param playerWasKilled true if the entity killed the player, false otherwise
     */
    public static void logPve(Player player, String entity, boolean playerWasKilled) {
        if (Config.General.isLogKills()) {
            try {

                Calendar cal = Calendar.getInstance();
                cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String content;
                if (playerWasKilled) {
                    content = "[" + sdf.format(cal.getTime()) + "] " + player.getName() + " was killed by " + entity;
                } else {
                    content = "[" + sdf.format(cal.getTime()) + "] " + entity + " was killed by " + player.getName();
                }
                File file = new File(MoneyThief.plugin.getDataFolder(), "MobKills.log");

                // if file doesn't exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw);
                out.println(content);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
