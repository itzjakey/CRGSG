package li.itzjakey.CrgSG.Rollback;

import li.itzjakey.CrgSG.Main;
import org.bukkit.Bukkit;

public class RollbackManager {

    public static void rollbackArena() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new RollbackTask());
    }

}
