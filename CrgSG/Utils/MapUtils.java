package li.itzjakey.CrgSG.Utils;

import li.itzjakey.CrgSG.Main;
import org.bukkit.Material;
import org.bukkit.World;

public class MapUtils {

    public static void addMap(String name, String author, String link, World world) {
        Main.getInstance().Data.set("Maps." + name + ".Author", author);
        Main.getInstance().Data.set("Maps." + name + ".Link", link);
        Main.getInstance().Data.set("Maps." + name + ".Number-Of-Spawns", 0);
        Main.getInstance().Data.set("Maps." + name + ".World", world.getName());

        Main.getInstance().getConfigManager().save();
    }

    public static String getFriendlyMapName(String s) {
        String str = s;
        str = str.replace('_', ' ');
        str = str.substring(0, 1).toUpperCase() +
                str.substring(1).toUpperCase();
        return str;
    }

}
