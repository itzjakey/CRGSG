package li.itzjakey.CrgSG.Listeners;

import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import li.itzjakey.CrgSG.Game.Game;
import li.itzjakey.CrgSG.Game.GameState;
import li.itzjakey.CrgSG.Main;
import li.itzjakey.CrgSG.Utils.DatabaseManager;
import li.itzjakey.CrgSG.Utils.MessageUtils;
import li.itzjakey.core.ucoins.UCoinAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageHandler implements Listener {

    public static boolean firstBlood = false;

    @EventHandler
    public void entityDamageEntity(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Player damaged = (Player) e.getEntity();
            
            int coinGain = Main.getInstance().Config.getInt("Kill-Coin-Reward");
            int firstBloodCoinGain = Main.getInstance().Config.getInt("First-Blood-Coin-Reward");
            
            if(Game.getInstance().getSpectating().contains(UUIDUtility.getUUID(damager.getName())) || Game.getInstance().getState() != GameState.In_Game || Game.getInstance().isGracePeriod()) {
                e.setCancelled(true);
            }

            if (damaged.getHealth() <= e.getDamage() && !firstBlood) {
                firstBlood = true;
                int gainedPoints;
                int lostPoints;

                if (DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(damaged.getName())) <= 100) {
                    lostPoints = 0;
                    gainedPoints = 10;
                }

                else {
                    lostPoints = DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(damaged.getName())) / 20 * 2;
                    gainedPoints = DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(damager.getName())) + 20 * 2;
                }

                if (Game.getInstance().getBounties().containsKey(UUIDUtility.getUUID(damaged.getName()))) {
                    gainedPoints += Game.getInstance().getBounties().get(UUIDUtility.getUUID(damaged.getName()));
                }

                Main.getInstance().getGameUtils().initializeSpectator(damaged);

                MessageUtils.messagePrefix(damaged, MessageUtils.MessageType.BAD, StringUtils.colorize("You were killed by &b&l" + damager.getName() + " &cand lost &b&l" + lostPoints + " &cpoints."));
                MessageUtils.messagePrefix(damaged, MessageUtils.MessageType.BAD, StringUtils.colorize("Your killer was on &b&l" + getPlayerHealth(damager) + " &chearts."));

                MessageUtils.messagePrefix(damager, MessageUtils.MessageType.GOOD, StringUtils.colorize("You killed &b&l" + damaged.getName() + " &aand gained &b&l" + gainedPoints + " &apoints!"));

                DatabaseManager.getInstance().addDeath(UUIDUtility.getUUID(damaged.getName()));
                DatabaseManager.getInstance().addKill(UUIDUtility.getUUID(damager.getName()));

                UCoinAPI.giveCoins(UUIDUtility.getUUID(damager.getName()), firstBloodCoinGain);
                DatabaseManager.getInstance().setPoints(UUIDUtility.getUUID(damager.getName()), gainedPoints);
                DatabaseManager.getInstance().setPoints(UUIDUtility.getUUID(damaged.getName()), lostPoints);

                Game.getInstance().broadcastGame(MessageUtils.MessageType.GOOD, "&b&l" + damager.getName() + " &c&lDREW FIRST BLOOD! (BONUS COINS + POINTS)");

                if (Game.getInstance().getInGame().size() == 1) {
                    Game.getInstance().endGame(true);
                }

                else if (Game.getInstance().getInGame().size() < 1) {
                    Game.getInstance().endGame(false);
                }
            }

            else if (damaged.getHealth() <= e.getDamage() && firstBlood) {
                int gainedPoints;
                int lostPoints;

                if (DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(damaged.getName())) <= 100) {
                    lostPoints = 0;
                    gainedPoints = 5;
                }

                else {
                    lostPoints = DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(damaged.getName())) / 20;
                    gainedPoints = DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(damager.getName())) + 20;
                }

                if (Game.getInstance().getBounties().containsKey(UUIDUtility.getUUID(damaged.getName()))) {
                    gainedPoints += Game.getInstance().getBounties().get(UUIDUtility.getUUID(damaged.getName()));
                }

                Main.getInstance().getGameUtils().initializeSpectator(damaged);

                MessageUtils.messagePrefix(damaged, MessageUtils.MessageType.BAD, StringUtils.colorize("You were killed by &b&l" + damager.getName() + " &cand lost &b&l" + lostPoints + " &cpoints."));
                MessageUtils.messagePrefix(damaged, MessageUtils.MessageType.BAD, StringUtils.colorize("Your killer was on &b&l" + getPlayerHealth(damager) + " &chearts."));

                MessageUtils.messagePrefix(damager, MessageUtils.MessageType.GOOD, StringUtils.colorize("You killed &b&l" + damaged.getName() + " &aand gained &b&l" + gainedPoints + " &apoints!"));

                if (Game.getInstance().getBounties().containsKey(UUIDUtility.getUUID(damaged.getName()))) {
                    MessageUtils.messagePrefix(damager, MessageUtils.MessageType.GOOD, StringUtils.colorize("You gained &c&l" + Game.getInstance().getBounties().get(UUIDUtility.getUUID(damaged.getName())) + " &aextra points from &b&l" + damaged.getName() + " &a's bounty!"));
                }

                DatabaseManager.getInstance().addDeath(UUIDUtility.getUUID(damaged.getName()));
                DatabaseManager.getInstance().addKill(UUIDUtility.getUUID(damager.getName()));

                UCoinAPI.giveCoins(UUIDUtility.getUUID(damager.getName()), coinGain);
                DatabaseManager.getInstance().setPoints(UUIDUtility.getUUID(damager.getName()), gainedPoints);
                DatabaseManager.getInstance().setPoints(UUIDUtility.getUUID(damaged.getName()), lostPoints);

                if (Game.getInstance().getInGame().size() == 1) {
                    Game.getInstance().endGame(true);
                }

                else if (Game.getInstance().getInGame().size() < 1) {
                    Game.getInstance().endGame(false);
                }
            }
        }
    }

    @EventHandler
    public void entityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player pl = (Player) e.getEntity();

            if (pl.getHealth() <= e.getDamage()) {
                if (Game.getInstance().getInGame().contains(UUIDUtility.getUUID(pl.getName()))) {
                    Game.getInstance().killPlayer(pl);
                    DatabaseManager.getInstance().addDeath(UUIDUtility.getUUID(pl.getName()));
                }
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
    
}
