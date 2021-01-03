package me.elgamer.vanillatpll;

import org.bukkit.plugin.java.JavaPlugin;

import me.elgamer.vanillatpll.commands.GetLocation;
import me.elgamer.vanillatpll.commands.Tpll;

public class Main extends JavaPlugin {
	
	@Override
	public void onEnable() {
	
	getCommand("tpll").setExecutor(new Tpll());
	getCommand("ll").setExecutor(new GetLocation());
	
	}

}
