package li.itzjakey.CrgSG.Listeners;

import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import li.itzjakey.CrgSG.Game.Game;
import li.itzjakey.CrgSG.Game.GameState;
import li.itzjakey.CrgSG.Main;
import li.itzjakey.CrgSG.Utils.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Join implements Listener {

    private List<UUID> permsOrNot = new ArrayList<>();

    @EventHandler
    public void preLogin(AsyncPlayerPreLoginEvent e) {
        if (Game.getInstance().getState() == GameState.In_Game) {
            if (Game.getInstance().getInGame().size() == Main.getInstance().Config.getInt("Max-Players")) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, StringUtils.colorize("&c&lKICKED: &7This server is currently full! Please try again later!"));
            }
        }
    }

    // terribly inefficient joining method, but eh... in a rush :P
    @EventHandler(priority = EventPriority.HIGHEST)
    public void login(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        e.setJoinMessage(null);

        if (!DatabaseManager.getInstance().doesExist(UUIDUtility.getUUID(player.getName()))) {
            DatabaseManager.getInstance().enterPlayer(UUIDUtility.getUUID(player.getName()));
        }

        if (Game.getInstance().getState() == GameState.Waiting || Game.getInstance().getState() == GameState.Voting) {

            /*
            if (Game.getInstance().getInGame().size() == Main.getInstance().Config.getInt("Max-Players") && !player.hasPermission("survivalgames.rank.pro") || !player.hasPermission("survivalgames.rank.elite")) {

                player.sendMessage(StringUtils.colorize("&e&m----------------------------------------------------"));
                player.sendMessage(StringUtils.colorize("&c&lKICKED: &7This server is currently full! Purchase &aPro or Elite rank &7for reserved slot!"));
                player.sendMessage(StringUtils.colorize("&e&m----------------------------------------------------"));

                Main.getBungeeManager().sendToServer(player, Main.getInstance().Config.getString("Main-Hub-Server-Name"));
            } else {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players.hasPermission("survivalgames.rank.pro") || players.hasPermission("survivalgames.rank.elite")) {
                        continue;
                    }

                    permsOrNot.add(UUIDUtility.getUUID(players.getName()));
                }

                int randomPlayer = Main.getInstance().rand().nextInt(permsOrNot.size());
                Player selectedPlayer = Bukkit.getPlayer(permsOrNot.get(randomPlayer));

                player.sendMessage(StringUtils.colorize("&e&m----------------------------------------------------"));
                player.sendMessage(StringUtils.colorize("&c&lKICKED: &cYou were kicked to make room for a donator or staff member."));
                player.sendMessage(StringUtils.colorize("&e&m----------------------------------------------------"));
                Main.getBungeeManager().sendToServer(selectedPlayer, Main.getInstance().Config.getString("Main-Hub-Server-Name"));
            }
            */

            Main.getInstance().getGameUtils().initializePlayer(player);

            if (Game.getInstance().getInGame().size() == Main.getInstance().Config.getInt("Minimum-Players") && Game.getInstance().getState() == GameState.Waiting) {
                Game.getInstance().startVoting();
            }
        }

        else if (Game.getInstance().getState().equals(GameState.In_Game)) {
            Main.getInstance().getGameUtils().initializeSpectator(player);
        }

        else {
            player.sendMessage(StringUtils.colorize("&e&m----------------------------------------------------"));
            player.sendMessage(StringUtils.colorize("&c&lKICKED: &7You cannot join the server at this time!"));
            player.sendMessage(StringUtils.colorize("&e&m----------------------------------------------------"));
            Main.getBungeeManager().sendToServer(player, Main.getInstance().Config.getString("Main-Hub-Server-Name"));
        }
    }

}

