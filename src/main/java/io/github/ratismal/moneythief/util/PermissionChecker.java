package io.github.ratismal.moneythief.util;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 * Created by Ratismal on 2015-10-06.
 */

public class PermissionChecker {

    public static boolean hasPermission(Player player, String permission) {
        Permission perm = new Permission(permission, PermissionDefault.FALSE);

        return player.hasPermission(perm);
    }

}
