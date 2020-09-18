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
		
		if (lat>90 || lat<-90) {
			p.sendMessage(ChatColor.RED + "Latitude is out of bounds, keep it between -90 and 90");
			return true;
		}
		
		if (lon>180 || lon<-180) {
			p.sendMessage(ChatColor.RED + "Longitude is out of bounds, keep it between -180 and 180");
			return true;
		}

		ModifiedAirocean projection = new ModifiedAirocean();

		double proj[] = projection.fromGeo(lon, lat);

		Location l;

		World world = p.getWorld();
		l = new Location(p.getWorld(), proj[0], world.getHighestBlockYAt((int) proj[0], (int) proj[1]), proj[1]);
		l = new Location(p.getWorld(), proj[0], world.getHighestBlockYAt(l), proj[1]);
		
		if (l.getY() == 0) {
			p.sendMessage(ChatColor.RED + "This location is above the void, you may not teleport here!");
			return true;
		}
		
		p.teleport(l);
		p.sendMessage("Teleported " + p.getName() + " to " + l.getX() + ", " + l.getY() + ", " + l.getZ());

		return true;
	}

}
