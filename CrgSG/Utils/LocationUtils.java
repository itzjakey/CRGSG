package li.itzjakey.CrgSG.Utils;

import li.itzjakey.CrgSG.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LocationUtils {

    public static Location getWaitingLobbyLocation() {
        return new Location(
                Bukkit.getWorld(Main.getInstance().Data.getString("LOBBY_SPAWN.WORLD")),
                Main.getInstance().Data.getDouble("LOBBY_SPAWN.X"),
                Main.getInstance().Data.getDouble("LOBBY_SPAWN.Y"),
                Main.getInstance().Data.getDouble("LOBBY_SPAWN.Z"),
                Main.getInstance().Data.getInt("LOBBY_SPAWN.YAW"),
                Main.getInstance().Data.getInt("LOBBY_SPAWN.PITCH")
        );
    }

    public static void setWaitingLobbyLocation(Location location) {
        Main.getInstance().Data.set("LOBBY_SPAWN.WORLD", location.getWorld().getName());
        Main.getInstance().Data.set("LOBBY_SPAWN.X", location.getX());
        Main.getInstance().Data.set("LOBBY_SPAWN.Y", location.getY());
        Main.getInstance().Data.set("LOBBY_SPAWN.Z", location.getZ());
        Main.getInstance().Data.set("LOBBY_SPAWN.YAW", location.getYaw());
        Main.getInstance().Data.set("LOBBY_SPAWN.PITCH", location.getPitch());

        Main.getInstance().getConfigManager().save();
        LoggingUtils.log("Lobby location set at " + (int) location.getX() + ", " + (int) location.getY() + ", " + (int) location.getZ() + "!");
    }

    public static List<Location> getDeathmatchSpawns() {
        List<Location> locations = new ArrayList<>();
        int number_of_spawns = Main.getInstance().Data.getInt("Deathmatch.Number-Of-Spawns");
        for(int x = 1; x <= number_of_spawns; x++) {
            locations.add(new Location(
                    Bukkit.getWorld(Main.getInstance().Data.getString("Deathmatch.Spawn" + x + ".WORLD")),
                    Main.getInstance().Data.getDouble("Deathmatch.Spawn" + x + ".X"),
                    Main.getInstance().Data.getDouble("Deathmatch.Spawn" + x + ".Y"),
                    Main.getInstance().Data.getDouble("Deathmatch.Spawn" + x + ".Z"),
                    Main.getInstance().Data.getInt("Deathmatch.Spawn" + x + ".YAW"),
                    Main.getInstance().Data.getInt("Deathmatch.Spawn" + x + ".PITCH")
            ));
        }

        return locations;
    }

    public static void addDeathmatchSpawn(Location location) {
        int spawn = Main.getInstance().Data.getInt("Deathmatch.Number-Of-Spawns") + 1;
        Main.getInstance().Data.set("Deathmatch.Spawn" + spawn + ".WORLD", location.getWorld().getName());
        Main.getInstance().Data.set("Deathmatch.Spawn" + spawn + ".X", location.getX());
        Main.getInstance().Data.set("Deathmatch.Spawn" + spawn + ".Y", location.getY());
        Main.getInstance().Data.set("Deathmatch.Spawn" + spawn + ".Z", location.getZ());
        Main.getInstance().Data.set("Deathmatch.Spawn" + spawn + ".YAW", location.getYaw());
        Main.getInstance().Data.set("Deathmatch.Spawn" + spawn + ".PITCH", location.getPitch());
        Main.getInstance().Data.set("Deathmatch.Number-Of-Spawns", spawn);
        Main.getInstance().getConfigManager().save();
        LoggingUtils.log("New deathmatch spawn set at " + (int) location.getX() + ", " + (int) location.getY() + ", " + (int) location.getZ() + "!");
    }

    public static String locToStr(Location loc) {
        DecimalFormat five = new DecimalFormat("#####.#####");
        DecimalFormat three = new DecimalFormat("#####.###");
        String x = String.valueOf(five.format(loc.getX()));
        String y = String.valueOf(three.format(loc.getY()));
        String z = String.valueOf(five.format(loc.getZ()));
        String yaw = String.valueOf(five.format(loc.getYaw()));
        String pitch = String.valueOf(five.format(loc.getPitch()));
        return x + "," + y + "," + z + "," + yaw + "," + pitch;
    }

    public static Location strToLoc(World world, String string) {
        String[] split = string.split(",");
        Double x = Double.parseDouble(split[0].trim());
        Double y = Double.parseDouble(split[1].trim());
        Double z = Double.parseDouble(split[2].trim());
        Float yaw = Float.parseFloat(split[3].trim());
        Float pitch = Float.parseFloat(split[3].trim());
        Location loc = new Location(world, x, y, z, yaw, pitch);
        return loc;
    }

}
