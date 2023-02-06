package me.elgamer.vanillatpll;

import me.elgamer.vanillatpll.commands.ll;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.elgamer.vanillatpll.commands.Tpll;

public class Main extends JavaPlugin {

    public static int MIN_Y;
    public static int MAX_Y;

    @Override
    public void onEnable() {

        //Get config.
        FileConfiguration config = getConfig();
        saveDefaultConfig();

        //Set max and min y.
        MIN_Y = config.getInt("min_y");
        MAX_Y = config.getInt("max_y");

        getCommand("tpll").setExecutor(new Tpll(config.getBoolean("permission_required"), this));
        getCommand("ll").setExecutor(new ll());

    }

}
