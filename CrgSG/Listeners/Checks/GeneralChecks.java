package li.itzjakey.CrgSG.Listeners.Checks;

import io.anw.Core.Bukkit.Utils.Misc.SoundPlayer;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import li.itzjakey.CrgSG.Game.Game;
import li.itzjakey.CrgSG.Game.GameState;
import li.itzjakey.CrgSG.Rollback.EditedBlock;
import li.itzjakey.CrgSG.Utils.BlockUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class GeneralChecks implements Listener {

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        if(!(Game.getInstance().getState() == GameState.In_Game) || Game.getInstance().getSpectating().contains(UUIDUtility.getUUID(e.getPlayer().getName()))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void pickup(PlayerPickupItemEvent e) {
        if(!(Game.getInstance().getState() == GameState.In_Game) || Game.getInstance().getSpectating().contains(UUIDUtility.getUUID(e.getPlayer().getName()))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        if(!(Game.getInstance().getState() == GameState.In_Game)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        e.setCancelled(true);

        if(Game.getInstance().getState() == GameState.In_Game) {
            if (BlockUtils.getAllowedBlocks().contains(e.getBlock().getType()) && !Game.getInstance().getSpectating().contains(UUIDUtility.getUUID(e.getPlayer().getName()))) {
                Game.getInstance().getChangedBlocks().add(new EditedBlock(e.getBlock().getWorld().getName(), e.getBlock().getType(), e.getBlock().getData(), Material.AIR, Byte.parseByte(0 + ""), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()));
                e.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();

        if (player.isOp() && Game.getInstance().getState() == GameState.In_Game && !Game.getInstance().getSpectating().contains(UUIDUtility.getUUID(player.getName()))) {
            e.setCancelled(false);
        }

        if (Game.getInstance().getState() == GameState.In_Game && !Game.getInstance().getSpectating().contains(UUIDUtility.getUUID(player.getName()))) {
            if (e.getBlock().getType() == Material.TNT) {
                e.getBlock().setType(Material.AIR);
                TNTPrimed tnt = e.getBlock().getWorld().spawn(e.getBlock().getLocation(), TNTPrimed.class);
                tnt.setFuseTicks(50);
                tnt.setIsIncendiary(false);
            }
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();

            if(!(Game.getInstance().getState() == GameState.In_Game) || Game.getInstance().getSpectating().contains(UUIDUtility.getUUID(player.getName())) || Game.getInstance().isGracePeriod()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void foodLoss(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();

            if(!(Game.getInstance().getState() == GameState.In_Game) || Game.getInstance().getSpectating().contains(UUIDUtility.getUUID(player.getName()))) {
                e.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        if(Game.getInstance().getState() == GameState.Starting) {
            if(e.getTo().getX() != e.getFrom().getX() || e.getTo().getZ() != e.getFrom().getZ()) {
                e.setTo(e.getFrom());
                SoundPlayer.play(e.getPlayer(), Sound.NOTE_BASS, 10);
            }
        }
    }

    @EventHandler
    public void creatureSpawn(CreatureSpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void invClick(InventoryClickEvent e) {
        if(!(Game.getInstance().getState() == GameState.In_Game) || Game.getInstance().getSpectating().contains(UUIDUtility.getUUID(e.getWhoClicked().getName()))) {
            e.setCancelled(true);
        }
    }

}
