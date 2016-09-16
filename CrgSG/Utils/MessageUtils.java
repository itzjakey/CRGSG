package li.itzjakey.CrgSG.Utils;

import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import li.itzjakey.CrgSG.Main;
import org.bukkit.entity.Player;

public class MessageUtils {

    public static void messagePrefix(Player player, MessageType type, String... messages) {
        for (String message : messages) {
            message(player, type.getPrefix() + message);
        }
    }

    public static void message(Player player, String... messages) {
        for (String message : messages) {
            player.sendMessage(StringUtils.colorize(message));
        }
    }

    public static enum MessageType {
        GOOD(StringUtils.colorize(Main.getInstance().getGameUtils().getPrefix() + " &a")),
        BAD(StringUtils.colorize(Main.getInstance().getGameUtils().getPrefix() + " &c"));

        private String prefix;

        MessageType(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return this.prefix;
        }
    }

}
