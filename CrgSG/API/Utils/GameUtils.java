package li.itzjakey.CrgSG.API.Utils;

import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class GameUtils {

    private String prefix;

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = StringUtils.colorize(prefix);
    }

    public void broadcast(String... messages) {
        for (String message : messages) {
            Bukkit.broadcastMessage(prefix + StringUtils.colorize(message));
        }
    }

    public abstract void initializePlayer(Player player);

    public abstract void unregisterPlayer(Player player);

    public abstract void initializeSpectator(Player player);

}
