package me.elgamer.vanillatpll.commands;

import me.elgamer.vanillatpll.Main;
import me.elgamer.vanillatpll.utils.Utils;
import net.buildtheearth.terraminusminus.dataset.IScalarDataset;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorPipelines;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.buildtheearth.terraminusminus.util.geo.CoordinateParseUtils;
import net.buildtheearth.terraminusminus.util.geo.LatLng;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Tpll implements CommandExecutor {

    private final boolean requires_permission;
    private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("##.#####");

    private final EarthGeneratorSettings bteGeneratorSettings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
    private final Main instance;

    public Tpll(boolean requires_permission, Main instance) {
        this.requires_permission = requires_permission;
        this.instance = instance;
    }

    /**
     * Gets all objects in a string array above a given index
     *
     * @param args  Initial array
     * @param index Starting index
     * @return Selected array
     */
    private String[] selectArray(String[] args, int index) {
        List<String> array = new ArrayList<>();
        for (int i = index; i < args.length; i++) {
            array.add(args[i]);
        }

        return array.toArray(array.toArray(new String[array.size()]));
    }

    private String[] inverseSelectArray(String[] args, int index) {
        List<String> array = new ArrayList<>();
        for (int i = 0; i < index; i++) {
            array.add(args[i]);
        }

        return array.toArray(array.toArray(new String[array.size()]));

    }

    /**
     * Gets a space seperated string from an array
     *
     * @param args A string array
     * @return The space seperated String
     */
    private String getRawArguments(String[] args) {
        if (args.length == 0) {
            return "";
        }
        if (args.length == 1) {
            return args[0];
        }

        StringBuilder arguments = new StringBuilder(args[0].replace((char) 176, (char) 32).trim());

        for (int x = 1; x < args.length; x++) {
            arguments.append(" ").append(args[x].replace((char) 176, (char) 32).trim());
        }

        return arguments.toString();
    }

    private void usage(Player p) {
        p.sendMessage(Utils.chat("&c/tpll <latitude> <longitude> [altitude]"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //Check if the sender is a player.
        //Only players can use /tpll.
        if (!(sender instanceof Player p)) {

            sender.sendMessage(Utils.chat("&cThis command can only be used by players."));
            return true;

        }

        //Check if permission is required.
        if (requires_permission) {

            if (!p.hasPermission("vanillatpll.tpll")) {

                p.sendMessage(Utils.chat("&cYou do not have permission to use this command."));
                return true;

            }
        }

        if (args.length == 0) {
            usage(p);
            return true;
        }

        double altitude = Double.NaN;
        LatLng defaultCoords = CoordinateParseUtils.parseVerbatimCoordinates(this.getRawArguments(args).trim());

        if (defaultCoords == null) {
            LatLng possiblePlayerCoords = CoordinateParseUtils.parseVerbatimCoordinates(this.getRawArguments(this.selectArray(args, 1)));
            if (possiblePlayerCoords != null) {
                defaultCoords = possiblePlayerCoords;
            }
        }

        LatLng possibleHeightCoords = CoordinateParseUtils.parseVerbatimCoordinates(this.getRawArguments(this.inverseSelectArray(args, args.length - 1)));
        if (possibleHeightCoords != null) {
            defaultCoords = possibleHeightCoords;
            try {
                altitude = Double.parseDouble(args[args.length - 1]);
            } catch (Exception ignored) {
            }
        }

        LatLng possibleHeightNameCoords = CoordinateParseUtils.parseVerbatimCoordinates(this.getRawArguments(this.inverseSelectArray(this.selectArray(args, 1), this.selectArray(args, 1).length - 1)));
        if (possibleHeightNameCoords != null) {
            defaultCoords = possibleHeightNameCoords;
            try {
                altitude = Double.parseDouble(this.selectArray(args, 1)[this.selectArray(args, 1).length - 1]);
            } catch (Exception ignored) {
            }
        }

        if (defaultCoords == null) {
            this.usage(p);
            return true;
        }

        double[] proj;

        try {
            proj = bteGeneratorSettings.projection().fromGeo(defaultCoords.getLng(), defaultCoords.getLat());
        } catch (Exception e) {
            this.usage(p);
            return true;
        }

        int alt = Utils.getHighestYAt(p.getWorld(), (int) proj[0], (int) proj[1]);
        CompletableFuture<Double> altFuture;

        if (alt == Integer.MIN_VALUE) {
            if (Double.isNaN(altitude)) {
                try {
                    altFuture = bteGeneratorSettings.datasets()
                            .<IScalarDataset>getCustom(EarthGeneratorPipelines.KEY_DATASET_HEIGHTS)
                            .getAsync(defaultCoords.getLng(), defaultCoords.getLat())
                            .thenApply(a -> a + 1.0d);
                } catch (OutOfProjectionBoundsException e) { //out of bounds, notify user
                    sender.sendMessage(Utils.error("These coordinates are out of the projection bounds."));
                    return true;
                }
            } else {
                altFuture = CompletableFuture.completedFuture(altitude);
            }
        } else {
            altFuture = CompletableFuture.supplyAsync(() -> (double) alt);
        }

        LatLng finalDefaultCoords = defaultCoords;
        altFuture.thenAccept(s -> Bukkit.getScheduler().runTask(instance, () -> {


            Location l = new Location(p.getWorld(), proj[0], s, proj[1]);
            instance.getLogger().info(l.getX() + "," + l.getZ());

            Location loc = new Location(p.getWorld(), (proj[0]), s, (proj[1]), p.getLocation().getYaw(), p.getLocation().getPitch());

            p.sendMessage(Utils.chat("&7Teleporting to &9" + DECIMAL_FORMATTER.format(finalDefaultCoords.getLat()) + "&7, &9" + DECIMAL_FORMATTER.format(finalDefaultCoords.getLng())));
            p.teleport(loc);

        }));

        return true;
    }
}
