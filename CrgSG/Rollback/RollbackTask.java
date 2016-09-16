package li.itzjakey.CrgSG.Rollback;

import li.itzjakey.CrgSG.Game.Game;
import li.itzjakey.CrgSG.Utils.LoggingUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.List;

public class RollbackTask implements Runnable {

    List<EditedBlock> data;

    public RollbackTask() {
        this.data = Game.getInstance().getChangedBlocks();
    }

    @Override
    public void run() {
        List<EditedBlock> data = this.data;

        for (int i = 0; i < data.size(); i++) {
            LoggingUtils.log("Resetting block: " + data.get(i).getPrevBlock().toString());
            Location l = new Location(Bukkit.getWorld(data.get(i).getWorld()), data.get(i).getX(), data.get(i).getY(), data.get(i).getZ());
            Block b = l.getBlock();
            b.setType(data.get(i).getPrevBlock());
            b.setData(data.get(i).getPrevBlockData());
            b.getState().update();
        }

        data.clear();
    }

}
