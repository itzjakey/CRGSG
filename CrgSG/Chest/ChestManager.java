package li.itzjakey.CrgSG.Chest;

import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import li.itzjakey.CrgSG.Game.Game;
import li.itzjakey.CrgSG.Game.GameState;
import li.itzjakey.CrgSG.Main;
import li.itzjakey.CrgSG.Utils.DatabaseManager;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class ChestManager implements Listener {

    private static List<BlockState> openedChests = new ArrayList<>();

    public static List<BlockState> getChests() {
        return openedChests;
    }

    @EventHandler(priority= EventPriority.HIGHEST)
    public void ChestListener(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            BlockState clicked = e.getClickedBlock().getState();

            if (((clicked instanceof Chest)) || ((clicked instanceof DoubleChest)))
                if ((Game.getInstance().getState() == GameState.In_Game)) {
                    if (!openedChests.contains(e.getClickedBlock().getState())) {
                        Inventory[] invs =
                                { ((DoubleChest) clicked).getLeftSide().getInventory(), ((clicked instanceof Chest)) ? ((Chest) clicked).getBlockInventory() :
                                   ((DoubleChest)clicked).getRightSide().getInventory() };
                        ItemStack item = invs[0].getItem(0);
                        int level = (item != null) && (item.getType() == Material.WOOL) ? item.getData().getData() + 1 : 1;
                        level = ChestRatioStorage.getInstance().getLevel(level);

                        for (Inventory inv : invs) {
                            inv.setContents(new ItemStack[inv.getContents().length]);
                        for (ItemStack i : ChestRatioStorage.getInstance().getItems(level)) {
                            int l = Main.getInstance().rand().nextInt(26);
                            while (inv.getItem(l) != null)
                                l = Main.getInstance().rand().nextInt(26);
                            inv.setItem(l, i);
                        }
                    }
                }

                openedChests.add(e.getClickedBlock().getState());
                DatabaseManager.getInstance().addChestOpened(UUIDUtility.getUUID(e.getPlayer().getName()));
             } else {
                 e.setCancelled(true);
            }
        }
    }
}