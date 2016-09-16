package li.itzjakey.CrgSG.Listeners;

import io.anw.Core.Bukkit.Utils.ItemStack.ItemUtils;
import io.anw.Core.Bukkit.Utils.Misc.SoundPlayer;
import io.anw.Core.Bukkit.Utils.Objects.InventoryMenu;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import li.itzjakey.CrgSG.Game.Game;
import li.itzjakey.CrgSG.Game.GameState;
import li.itzjakey.CrgSG.Main;
import li.itzjakey.CrgSG.Objects.SGMap;
import li.itzjakey.CrgSG.Utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;

public class LobbyItems implements Listener {

    @EventHandler
    public void clockInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        try {
            if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) return;
            if (e.getItem().getType().equals(Material.WATCH) && e.getItem().getItemMeta().getDisplayName().contains("Return to Lobby")) {
                Main.getBungeeManager().sendToServer(player, Main.getInstance().Config.getString("Main-Hub-Server-Name"));
            }
        } catch (NullPointerException ignored) {  }
    }

    @EventHandler
    public void voteMenu(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        try {
            if(!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) return;
            if(e.getItem().getType().equals(Material.ENCHANTED_BOOK) && e.getItem().getItemMeta().getDisplayName().contains("Map Voting")) {
                if(Game.getInstance().getState() == GameState.Voting) {
                    if(!Game.getInstance().getPlayerMapVotes().containsKey(UUIDUtility.getUUID(player.getName()))) {
                        InventoryMenu menu = new InventoryMenu("Map Voting", 1);
                        for(SGMap map : SGMap.getAllMaps()) {
                            menu.addItem(ItemUtils.createItemStack("&b&l" + map.getName(), Arrays.asList("&7Vote for the map &6" + map.getName() + " &7by &6" + map.getAuthor() + "&7!"), Material.EMPTY_MAP));
                        }
                        menu.addItem(ItemUtils.createItemStack("&b&l&oRandom Map", Arrays.asList("&7Vote for a random map!"), Material.MAP));

                        menu.open(player);
                        SoundPlayer.play(player, Sound.NOTE_PLING, 5);
                    } else {
                        MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You have already voted for a map!");
                        SoundPlayer.play(player, Sound.NOTE_BASS, 10);
                    }
                } else {
                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Please wait until the Voting stage to vote!");
                    SoundPlayer.play(player, Sound.NOTE_BASS, 10);
                }
            }
        } catch (NullPointerException ignored) {  }
    }

    @EventHandler
    public void voteMenuClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getInventory().getTitle().equalsIgnoreCase("Map Voting")) {
            e.setCancelled(true);
            player.closeInventory();

            if (e.getCurrentItem().hasItemMeta()) {
                SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 5);
                String title = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                SGMap map;

                if (title.contains("Random Map")) {
                    List<SGMap> maps = Arrays.asList(SGMap.getAllMaps());
                    int rand = Main.getInstance().rand().nextInt(maps.size());
                    map = Game.getInstance().getMapInVotes(maps.get(rand).getName());
                    Game.getInstance().getPlayerMapVotes().put(UUIDUtility.getUUID(player.getName()), map);
                    Game.getInstance().getMapInVotes(ChatColor.stripColor(map.getName()));
                } else {
                    map = Game.getInstance().getMapInVotes(ChatColor.stripColor(title));
                    Game.getInstance().getPlayerMapVotes().put(UUIDUtility.getUUID(player.getName()), map);
                }

                Game.getInstance().broadcastGame(MessageUtils.MessageType.GOOD, "&b&l" + player.getName() + " &avoted for map &b" + map.getName() + "&7! &7(&ax" + Game.getInstance().getVoteValue(player) + "!&7)");
                int cv = Game.getInstance().getMapVotes().get(map);
                Game.getInstance().getMapVotes().remove(map);
                Game.getInstance().getMapVotes().put(map, cv + Game.getInstance().getVoteValue(player));
            }

            Game.getInstance().updateScoreboardVotes();
        }
    }

}
