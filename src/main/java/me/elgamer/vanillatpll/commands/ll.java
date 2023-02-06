package me.elgamer.vanillatpll.commands;

import me.elgamer.vanillatpll.utils.Utils;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class ll implements CommandExecutor {

    private final EarthGeneratorSettings bteGeneratorSettings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
    private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("##.#####");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //Check if the sender is a player.
        if (!(sender instanceof Player p)) {

            sender.sendMessage(Utils.chat("&cThis command can only be run by a player."));
            return true;

        }

        try {

            double[] coords = bteGeneratorSettings.projection().toGeo(p.getLocation().getX(), p.getLocation().getZ());

            p.sendMessage(Utils.chat("&aYour coordinates are &3" + DECIMAL_FORMATTER.format(coords[1]) + "," + DECIMAL_FORMATTER.format(coords[0])));
            TextComponent message = new TextComponent(Utils.chat("&aClick here to view the coordinates in Google Maps."));
            message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.google.com/maps/@?api=1&map_action=map&basemap=satellite&zoom=21&center=" + coords[1] + "," + coords[0]));
            p.spigot().sendMessage(message);
            return true;

        } catch (OutOfProjectionBoundsException e) {

            p.sendMessage(Utils.chat("&cThis location is outside the projection bounds."));
            return true;

        }
    }
}
