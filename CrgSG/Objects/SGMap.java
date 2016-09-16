package li.uprising.CrgSG.Objects;

import li.uprising.CrgSG.Main;
import li.uprising.CrgSG.Utils.LocationUtils;
import li.uprising.CrgSG.Utils.LoggingUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class SGMap {

    private String name;
    private String author;
    private String link;

    public SGMap(String name, String author, String link) {
        if (Main.getInstance().Data.getString("Maps." + name) == null) {
            throw new NullPointerException("Map is null");
        }

        this.name = name;
        this.author = author;
        this.link = link;
    }

    public String getName() {
        return this.name;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getLink() {
        return this.link;
    }

    public World getWorld() {
        return Bukkit.getWorld(Main.getInstance().Data.getString("Maps." + getName() + ".World"));
    }

    public void addSpawn(Location loc) {
        List<String> spawns = new ArrayList<>();
        spawns.addAll(Main.getInstance().Data.getStringList("Maps." + getName() + ".Spawns"));
        spawns.add(LocationUtils.locToStr(loc));

        Main.getInstance().Data.set("Maps." + getName() + ".Spawns", spawns);
        Main.getInstance().getConfigManager().save();
    }


    public List<Location> getSpawns() {
        List<Location> locs = new ArrayList<>();

        for (int i = 0; i < Main.getInstance().Data.getStringList("Maps." + getName() + ".Spawns").size(); i++) {
            locs.add(LocationUtils.strToLoc(getWorld(), Main.getInstance().Data.getStringList("Maps." + getName() + ".Spawns").get(i)));
        }

        return locs;
    }

    /*
    public void addSpawn(Location location) {
        int spawn = Main.getInstance().Data.getInt("Maps." + name + ".Number-Of-Spawns") + 1;
        Main.getInstance().Data.set("Maps." + name + ".Spawn" + spawn + ".WORLD", location.getWorld().getName());
        Main.getInstance().Data.set("Maps." + name + ".Spawn" + spawn + ".X", location.getX());
        Main.getInstance().Data.set("Maps." + name + ".Spawn" + spawn + ".Y", location.getY());
        Main.getInstance().Data.set("Maps." + name + ".Spawn" + spawn + ".Z", location.getZ());
        Main.getInstance().Data.set("Maps." + name + ".Spawn" + spawn + ".YAW", location.getYaw());
        Main.getInstance().Data.set("Maps." + name + ".Spawn" + spawn + ".PITCH", location.getPitch());
        Main.getInstance().Data.set("Maps." + name + ".Number-Of-Spawns", spawn);
        Main.getInstance().getConfigManager().save();
        LoggingUtils.log("New spawn location for map " + name + " set at " + (int) location.getX() + ", " + (int) location.getY() + ", " + (int) location.getZ() + "!");
    }

    public List<Location> getSpawns() {
        List<Location> locations = new ArrayList<>();
        int number_of_spawns = Main.getInstance().Data.getInt("Maps." + name + ".Number-Of-Spawns");
        for(int x = 1; x <= number_of_spawns; x++) {
            locations.add(new Location(
                    Bukkit.getWorld(Main.getInstance().Data.getString("Maps." + name + ".Spawn" + x + ".WORLD")),
                    Main.getInstance().Data.getDouble("Maps." + name + ".Spawn" + x + ".X"),
                    Main.getInstance().Data.getDouble("Maps." + name + ".Spawn" + x + ".Y"),
                    Main.getInstance().Data.getDouble("Maps." + name + ".Spawn" + x + ".Z"),
                    Main.getInstance().Data.getInt("Maps." + name + ".Spawn" + x + ".YAW"),
                    Main.getInstance().Data.getInt("Maps." + name + ".Spawn" + x + ".PITCH")
            ));
        }

        return locations;
    }
    */

    public static SGMap getMap(String name) {
        if(Main.getInstance().Data.getConfigurationSection("Maps." + name) == null) {
            throw new NullPointerException("Map is null");
        }

        return new SGMap(
                name,
                Main.getInstance().Data.getString("Maps." + name + ".Author"),
                Main.getInstance().Data.getString("Maps." + name + ".Link")
        );
    }

    public static SGMap[] getAllMaps() {
        List<SGMap> mapList = new ArrayList<>();
        for(String map : Main.getInstance().Data.getConfigurationSection("Maps").getKeys(false)) {
            mapList.add(new SGMap(
                    map,
                    Main.getInstance().Data.getString("Maps." + map + ".Author"),
                    Main.getInstance().Data.getString("Maps." + map + ".Link")
            ));
        }

        return mapList.toArray(new SGMap[mapList.size()]);
    }

    public static String getFriendlyMapName(String s) {
        String str = s;
        str = str.replace('_', ' ');
        return str;
    }

}
