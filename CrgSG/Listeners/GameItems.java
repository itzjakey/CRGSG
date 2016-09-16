package li.itzjakey.CrgSG.Listeners;

import io.anw.Core.Bukkit.Utils.ItemStack.ItemUtils;
import io.anw.Core.Bukkit.Utils.Misc.SoundPlayer;
import io.anw.Core.Bukkit.Utils.Objects.InventoryMenu;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import li.itzjakey.CrgSG.Game.Game;
import li.itzjakey.CrgSG.Utils.DatabaseManager;
import li.itzjakey.CrgSG.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.UUID;

public class GameItems implements Listener {

    @EventHandler
    public void targetSelectorItem(PlayerInteractEvent e) {
        final Player player = e.getPlayer();

        try {
            if(!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) return;
            if (e.getItem().getType().equals(Material.COMPASS) && e.getItem().getItemMeta().getDisplayName().contains("Tribute Tracker")
                    || e.getItem().getType().equals(Material.COMPASS) && e.getItem().getItemMeta().getDisplayName().contains("Tracking")) {
                double closest = 500000.0D;
                Player close = null;

                for (UUID uuid : Game.getInstance().getInGame()) {
                    Player iPlayer = Bukkit.getPlayer(uuid);
                    if (player.getLocation().distance(iPlayer.getLocation()) < closest && !iPlayer.getName().equalsIgnoreCase(player.getName())) {
                        closest = player.getLocation().distance(iPlayer.getLocation());
                        close = iPlayer;
                    }
                }

                if (close != null) {
                    String display = ChatColor.RESET + "       " +
                            ChatColor.RED + "Pointing to: " +
                            ChatColor.DARK_RED + close.getName() +
                            ChatColor.RESET + "     " + ChatColor.RED +
                            "Distance: " + ChatColor.DARK_RED +
                            format(closest);
                    e.getItem().getItemMeta().setDisplayName(display);
                    player.setCompassTarget(close.getLocation());
                    e.getItem().setItemMeta(e.getItem().getItemMeta());
                }
            }

        } catch (NullPointerException ignored) {  }
    }

    public void spectateMenuOpen(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        try {
            if(!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) return;
            if(e.getItem().getType().equals(Material.COMPASS) && e.getItem().getItemMeta().getDisplayName().contains("Spectate Players")) {
                InventoryMenu menu = new InventoryMenu("Spectate Menu", 3);
                for(int x = 0; x < Game.getInstance().getInGame().size(); x++) {
                    if (Bukkit.getPlayer(Game.getInstance().getInGame().get(x)) != player) {
                        menu.addItem(ItemUtils.createItemStack("&b&l" + Game.getInstance().getInGame().get(x), Arrays.asList("&7Click to spectate this player!", "&8&m------------------------", "&cHealth &l: &f" + getPlayerHealth(player) + " ❤", "&6Food &l: &f" + player.getFoodLevel(), "&8&m------------------------", "&aKills &l: &f" + DatabaseManager.getInstance().getKills(UUIDUtility.getUUID(player.getName())), "&bDeaths &l: &f" + DatabaseManager.getInstance().getDeaths(UUIDUtility.getUUID(player.getName())), "&eWins &l: &f" + DatabaseManager.getInstance().getWins(UUIDUtility.getUUID(player.getName()))), Material.SKULL_ITEM));
                        menu.getInventory().getItem(x).setDurability((short) 3);
                    }
                }

                menu.open(player);
            }
        } catch (NullPointerException ignored) {  }
    }

    @EventHandler
    public void spectateMenuClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if(e.getInventory().getTitle().contains("Spectating Menu")) {
            e.setCancelled(true);
            if(e.getCurrentItem().hasItemMeta()) {
                SoundPlayer.play(player, Sound.ENDERMAN_TELEPORT, 5);
                Player target = Bukkit.getPlayer(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                player.teleport(target);
                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Teleported to &b" + UUIDUtility.getUUID(target.getName()) + "&a!");
            }
        }
    }

    // This is a fucking horrid method. Will rewrite later lol.
    public double getPlayerHealth(Player p) {
        double h = p.getHealth();

        if (h % 2.0D == 0.0D) {
            return h / 2.0D;
        } if (h == 19.0D) {
            return 9.5D;
        } if (h == 17.0D) {
            return 8.5D;
        } if (h == 15.0D) {
            return 7.5D;
        } if (h == 13.0D) {
            return 6.5D;
        } if (h == 11.0D) {
            return 5.5D;
        } if (h == 9.0D) {
            return 4.5D;
        } if (h == 7.0D) {
            return 3.5D;
        } if (h == 5.0D) {
            return 2.5D;
        } if (h == 3.0D) {
            return 1.5D;
        } if (h == 1.0D) {
            return 0.5D;
        }

        return h;
    }

    private static String format(double d) {
        DecimalFormat format = new DecimalFormat("##.#");
        return format.format(d);
    }

}
