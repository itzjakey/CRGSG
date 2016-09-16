package li.itzjakey.CrgSG.API.Utils;

import io.anw.Core.Bukkit.Utils.Chat.MessageUtils;
import io.anw.Core.Bukkit.Utils.Misc.SoundPlayer;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import li.itzjakey.CrgSG.API.GamePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Conditions implements Listener {

    private List<UUID> hiddenPlayers = new ArrayList<>(), noChat = new ArrayList<>();
    private boolean chatEnabledGlobally = true, canMove = true;
    private GamePlugin plugin;

    public Conditions(GamePlugin plugin) {
        this.plugin = plugin;
    }

    public List<UUID> getHiddenPlayers() {
        return this.hiddenPlayers;
    }

    public List<UUID> getNoChat() {
        return this.noChat;
    }

    public boolean isChatEnabledGlobally() {
        return !this.chatEnabledGlobally;
    }

    public void setChatEnabledGlobally(boolean flag) {
        this.chatEnabledGlobally = flag;
    }

    public boolean canPlayersMove() {
        return !this.canMove;
    }

    public void setCanPlayersMove(boolean flag) {
        this.canMove = flag;
    }

    public void setNoChatIndividual(Player player, boolean flag) {
        if (flag) {
            getNoChat().add(UUIDUtility.getUUID(player.getName()));
        } else {
            if (getNoChat().contains(UUIDUtility.getUUID(player.getName()))) {
                getNoChat().remove(UUIDUtility.getUUID(player.getName()));
            }
        }
    }

    public void addHiddenPlayer(Player player) {
        getHiddenPlayers().add(UUIDUtility.getUUID(player.getName()));
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online != player) {
                online.hidePlayer(player);
            }
        }
        SoundPlayer.play(player, Sound.NOTE_BASS, 25);
        MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You are now &6&lINVISIBLE&7!");
    }

    public void removeHiddenPlayer(Player player) {
        getHiddenPlayers().remove(UUIDUtility.getUUID(player.getName()));
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online != player) {
                online.showPlayer(player);
            }
        }
        SoundPlayer.play(player, Sound.NOTE_PLING, 25);
        MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "You are now &6&lVISIBLE&7!");
    }

    /*
     * LISTENERS
     */

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (getHiddenPlayers().contains(UUIDUtility.getUUID(online.getName())) && online != player) {
                player.hidePlayer(online);
            }
        }
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (isChatEnabledGlobally()) {
            e.setCancelled(true);
            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You cannot chat at this time!");
            SoundPlayer.play(player, Sound.NOTE_BASS, 25);
        } else if (getNoChat().contains(UUIDUtility.getUUID(player.getName()))) {
            e.setCancelled(true);
            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You cannot chat at this time!");
            SoundPlayer.play(player, Sound.NOTE_BASS, 25);
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        if (canPlayersMove()) {
            if (e.getTo().getX() != e.getFrom().getX() || e.getTo().getZ() != e.getFrom().getZ()) {
                e.setTo(e.getFrom());
            }
        }
    }

}
