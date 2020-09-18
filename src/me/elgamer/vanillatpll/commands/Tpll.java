package me.elgamer.vanillatpll.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.vanillatpll.projections.ModifiedAirocean;

public class Tpll implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		//Check is command sender is a player
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cYou cannot add a player to a region!");
			return true;
		}

		//Convert sender to player
		Player p = (Player) sender;

		if (!(p.hasPermission("vanillatpll.tpll"))) {
			p.sendMessage(ChatColor.RED + "You do not have permission for this command!");
			return true;
		}

		if(args.length==0) {
			return false;
		}

		String[] splitCoords = args[0].split(",");
		String alt = null;

		if(splitCoords.length==2&&args.length<3) { // lat and long in single arg
			if(args.length>1) alt = args[1];
			args = splitCoords;
		} else if(args.length==3) {
			alt = args[2];
		}

		if(args[0].endsWith(",")) {
			args[0] = args[0].substring(0, args[0].length() - 1);
		}

		if(args.length>1&&args[1].endsWith(",")) {
			args[1] = args[1].substring(0, args[1].length() - 1);
		}

		if(args.length!=2&&args.length!=3) {
			return false;
		}

		double lon, lat;

		try {
			lat = Double.parseDouble(args[0]);
			lon = Double.parseDouble(args[1]);
			if(alt!=null) alt = Double.toString(Double.parseDouble(alt));
		} catch(Exception e) {
			return false;
		}

		ModifiedAirocean projection = new ModifiedAirocean();

		double proj[] = projection.fromGeo(lon, lat);
		p.sendMessage("x: " + proj[0] + " | z: " + proj[1]);

		Location l;

		World world = p.getWorld();
		l = new Location(p.getWorld(), proj[0], world.getHighestBlockYAt((int) proj[0], (int) proj[1]), proj[1]);

		p.teleport(l);

		return true;
	}

}
