package me.elgamer.vanillatpll.utils;

import me.elgamer.vanillatpll.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;

public class Utils {

    public static String chat(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String error(String message) {
        return chat("&c" + message);
    }

    public static String success(String message) {
        return chat("&a" + message);
    }

    public static int getHighestYAt(World w, int x, int z) {

        for (int i = (Main.MAX_Y-1); i >= Main.MIN_Y; i--) {
            if (w.getBlockAt(x, i, z).getType() != Material.AIR) {
                return i + 1;
            }
        }
        return Integer.MIN_VALUE;
    }
}