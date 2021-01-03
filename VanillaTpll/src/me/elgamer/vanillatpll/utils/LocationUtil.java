package me.elgamer.vanillatpll.utils;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {
 
    @SuppressWarnings("deprecation")
	public static boolean isBlockAboveAir(final World world, final int x, final int y, final int z) {
        return (y > world.getMaxHeight() || world.getBlockAt(x, y - 1, z).getType().getId() == 0);
    }

    public static Location getSafeDestination(final Location loc) throws Exception {

        final World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = (int) Math.round(loc.getY());
        int z = loc.getBlockZ();

        while (isBlockAboveAir(world, x, y, z)) {
            y -= 1;
            if (y < 0) {
                y = 0;
                break;
            }
        }

        return new Location(world, x + 0.5, y, z + 0.5);
    }

}