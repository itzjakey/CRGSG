package li.itzjakey.CrgSG.Listeners;

import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import li.itzjakey.CrgSG.Game.Game;
import li.itzjakey.CrgSG.Game.GameState;
import li.itzjakey.CrgSG.Objects.SGMap;
import li.itzjakey.CrgSG.Utils.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Leave implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void quit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        e.setQuitMessage(null);

        if (Game.getInstance().getState() == GameState.In_Game || Game.getInstance().getState() == GameState.Starting) {
            if (Game.getInstance().getInGame().contains(UUIDUtility.getUUID(player.getName()))) {
                Game.getInstance().killPlayer(player);
                DatabaseManager.getInstance().addDeath(UUIDUtility.getUUID(player.getName()));

                if(Game.getInstance().getInGame().size() == 1) {
                    Game.getInstance().endGame(true);
                }

                else if(Game.getInstance().getInGame().size() < 1) {
                    Game.getInstance().endGame(false);
                }
            }
        }

        Game.getInstance().removeFromGame(player);
        Game.getInstance().removeFromSpectator(player);

        if(Game.getInstance().getState() == GameState.Voting && Game.getInstance().getPlayerMapVotes().containsKey(UUIDUtility.getUUID(player.getName()))) {
            SGMap map = Game.getInstance().getPlayerMapVotes().get(UUIDUtility.getUUID(player.getName()));
            Game.getInstance().getPlayerMapVotes().remove(UUIDUtility.getUUID(player.getName()));

            int votes = Game.getInstance().getMapVotes().get(map);
            Game.getInstance().getMapVotes().remove(map);
            Game.getInstance().getMapVotes().put(map, votes - Game.getInstance().getVoteValue(player));
            Game.getInstance().updateScoreboardVotes();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void kick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        e.setLeaveMessage(null);

        if(Game.getInstance().getState() == GameState.In_Game || Game.getInstance().getState() == GameState.Starting) {
            if(Game.getInstance().getInGame().contains(UUIDUtility.getUUID(player.getName()))) {
                Game.getInstance().killPlayer(player);
                DatabaseManager.getInstance().addDeath(UUIDUtility.getUUID(player.getName()));

                if(Game.getInstance().getInGame().size() == 1) {
                    Game.getInstance().endGame(true);
                }

                else if(Game.getInstance().getInGame().size() < 1) {
                    Game.getInstance().endGame(false);
                }
            }
        }

        Game.getInstance().removeFromGame(player);
        Game.getInstance().removeFromSpectator(player);

        if(Game.getInstance().getState() == GameState.Voting && Game.getInstance().getPlayerMapVotes().containsKey(UUIDUtility.getUUID(player.getName()))) {
            SGMap map = Game.getInstance().getPlayerMapVotes().get(UUIDUtility.getUUID(player.getName()));
            Game.getInstance().getPlayerMapVotes().remove(UUIDUtility.getUUID(player.getName()));

            int votes = Game.getInstance().getMapVotes().get(map);
            Game.getInstance().getMapVotes().remove(map);
            Game.getInstance().getMapVotes().put(map, votes - Game.getInstance().getVoteValue(player));
            Game.getInstance().updateScoreboardVotes();
        }
    }

}
