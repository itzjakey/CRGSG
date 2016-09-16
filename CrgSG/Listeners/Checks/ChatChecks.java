package li.itzjakey.CrgSG.Listeners.Checks;

import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import li.itzjakey.CrgSG.Game.Game;
import li.itzjakey.CrgSG.Utils.DatabaseManager;
import li.itzjakey.CrgSG.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatChecks implements Listener {

    private static Map<UUID, Boolean> firstChat = new HashMap<>();

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (Game.getInstance().getSpectating().contains(UUIDUtility.getUUID(e.getPlayer().getName()))) {
            if (!firstChat.containsKey(UUIDUtility.getUUID(player.getName())) && !firstChat.get(UUIDUtility.getUUID(player.getName())).equals(true)) {
                MessageUtils.messagePrefix(e.getPlayer(), MessageUtils.MessageType.BAD, StringUtils.colorize("&lNOTE: &cYou can only chat with other spectators!"));
                firstChat.put(UUIDUtility.getUUID(player.getName()), true);
            }

            for (UUID uuid : Game.getInstance().getInGame()) {
                e.getRecipients().remove(Bukkit.getPlayer(uuid));
            }

            e.setFormat(StringUtils.colorize("&c&lSpectator &7: &6(&e" + DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(player.getName())) + "&6) ") + e.getFormat());
        }

        e.setFormat(StringUtils.colorize("&6(&e" + DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(player.getName())) + "&6) ") + e.getFormat());
    }

}
