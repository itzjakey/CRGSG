package li.itzjakey.CrgSG.Commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import li.itzjakey.CrgSG.Game.Game;
import li.itzjakey.CrgSG.Game.GameState;
import li.itzjakey.CrgSG.Objects.SGMap;
import li.itzjakey.CrgSG.Utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Commands {

    private static final char RAQUO = '»';

    @Command(
            aliases = {"setlobby"},
            desc = "Set the lobby of the game",
            max = 0
    )
    @CommandPermissions({"op"})
    public static void setLobby(CommandContext args, CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            LocationUtils.setWaitingLobbyLocation(player.getLocation());
            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Set the main lobby spawn!");
        }
    }

    @Command(
            aliases = {"addmap"},
            desc = "Add a map to the Survival Games!",
            min = 3,
            max = 3,
            usage = "[name] [author] [link]"
    )
    @CommandPermissions({"op"})
    public static void addMap(CommandContext args, CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            String name = args.getString(0), author = args.getString(1), link = args.getString(2);
            World world = player.getLocation().getWorld();
            MapUtils.addMap(name, author, link, world);

            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Added map &6" + name + " &7in world &6" + world + " &7by &6" + author + " &7with the link &6" + link + "&7!");
            LoggingUtils.log("Successfully added map " + name + " by author " + author + " with link " + link + " at world " + world + "!");
        }
    }

    @Command(
            aliases = {"bounty"},
            desc = "Add bounties to players in Survival Games!",
            min = 2,
            max = 2,
            usage = "[name] [points]"
    )
    @CommandPermissions({"op"})
    public static void bounty(CommandContext args, CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            String name = args.getString(0), points = args.getString(1);

            if (Game.getInstance().getState() == GameState.In_Game) {
                if (Bukkit.getPlayer(UUIDUtility.getUUID(name)) != null) {
                    if (!Game.getInstance().getSpectating().contains(UUIDUtility.getUUID(name))) {
                        if (Integer.valueOf(points) > 0) {
                            if (Integer.valueOf(points) <= DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(player.getName()))) {
                                Game.getInstance().getBounties().put(UUIDUtility.getUUID(name), Integer.valueOf(points));
                                DatabaseManager.getInstance().setPoints(UUIDUtility.getUUID(player.getName()), DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(player.getName())) - Integer.valueOf(points));
                                Game.getInstance().broadcastGame("&c&l" + player.getName() + " &ahas just placed a bounty of &c&l" + points + " &apoints on player &c&l" + name + "&a!");
                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "You have just placed a bounty of &c&l" + points + " &apoints on the player &c&l" + name + "&a!");
                            } else {
                                MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You do not have that many points to bounty on this player!");
                            }
                        } else {
                            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "Your bounty must be higher than 0!");
                        }
                    } else {
                        MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You cannot place bounties on non-tributes!");
                    }
                } else {
                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "That player is not online!");
                }
            } else {
                MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "You cannot do this until the game starts!");
            }

            Game.getInstance().broadcastGame("&c&l" + player.getName() + " &ahas just placed a bounty of &c&l" + points + " &apoints on player &c&l" + name + "&a!");
        }
    }

    @Command(
            aliases = {"addspawn"},
            desc = "Add a spawnpoint for a map!",
            min = 1,
            max = 1,
            usage = "[mapname]"
    )
    @CommandPermissions({"op"})
    public static void addSpawn(CommandContext args, CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Location l = player.getLocation();
            SGMap map = SGMap.getMap(args.getString(0));

            if (map.getSpawns().contains(l)) {
                MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "A spawn already exists at this location!");
                return;
            } else {
                map.addSpawn(l);
                MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Added a spawn to map &6" + map.getName() + "!");
            }
        }
    }

    @Command(
            aliases = {"adddeathmatchspawn"},
            desc = "Add a spawnpoint for the deathmatch arena!",
            max = 0
    )
    @CommandPermissions({"op"})
    public static void addDmSpawn(CommandContext args, CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Location l = player.getLocation();
            LocationUtils.addDeathmatchSpawn(l);
            MessageUtils.messagePrefix(player, MessageUtils.MessageType.GOOD, "Added a spawn for the deathmatch arena!");
        }
    }


    @Command(
            aliases = {"stats"},
            desc = "Check a player's Survival Games stats!",
            min = 1,
            max = 1,
            usage = "[player]"
    )
    @CommandPermissions({"op"})
    public static void stats(CommandContext args, CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            String targetName = args.getString(0);

            //UUID player_uuid = UUIDUtility.getUUID(player.getName());
            UUID target_uuid = UUIDUtility.getUUID(targetName);

            if (args.argsLength() == 1) {
                MessageUtils.message(
                        player,
                        "&e&m----------------------------------------------------",
                        " &e&lKills &7" + RAQUO + " &a&l" + DatabaseManager.getInstance().getKills(target_uuid),
                        " &e&lDeaths &7" + RAQUO + " &a&l" + DatabaseManager.getInstance().getDeaths(target_uuid),
                        " &e&lWins &7" + RAQUO + " &a&l" + DatabaseManager.getInstance().getWins(target_uuid),
                        " &e&lPoints &7" + RAQUO + " &a&l" + DatabaseManager.getInstance().getPoints(target_uuid),
                        " &e&lGames Played &7" + RAQUO + " &a&l" + DatabaseManager.getInstance().getGamesPlayed(target_uuid),
                        " &e&lChests Opened &7" + RAQUO + " &a&l" + DatabaseManager.getInstance().getChestsOpened(target_uuid),
                        "&e&m----------------------------------------------------"
                );
            }
        }
    }

}
