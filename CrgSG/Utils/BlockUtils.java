package li.itzjakey.CrgSG.Utils;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class BlockUtils {

    public static List<Material> getAllowedBlocks() {
        Material[] blocks = new Material[] {
                Material.LONG_GRASS,
                Material.MELON_BLOCK,
                Material.LEAVES,
                Material.LEAVES_2,
                Material.YELLOW_FLOWER,
                Material.RED_ROSE,
                Material.DOUBLE_PLANT,
        };

        return Arrays.asList(blocks);
    }

}
