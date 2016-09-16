package li.itzjakey.CrgSG.Game;

import io.anw.Core.Bukkit.Utils.Chat.StringUtils;
import io.anw.Core.Bukkit.Utils.ItemStack.ItemUtils;
import io.anw.Core.Bukkit.Utils.Misc.BossBarUtils;
import io.anw.Core.Bukkit.Utils.Misc.FireworkEffectPlayer;
import io.anw.Core.Bukkit.Utils.Misc.SoundPlayer;
import io.anw.Core.Bukkit.Utils.Objects.Particle;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import li.itzjakey.CrgSG.Chest.ChestManager;
import li.itzjakey.CrgSG.Main;
import li.itzjakey.CrgSG.Objects.SGMap;
import li.itzjakey.CrgSG.Rollback.EditedBlock;
import li.itzjakey.CrgSG.Utils.*;
import li.itzjakey.core.ucoins.UCoinAPI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.logging.Level;

public class Game {

    private static Game instance = new Game();
    public static Game getInstance() {
        return instance;
    }

    private List<UUID> inGame = new ArrayList<>();
    private List<UUID> spectating = new ArrayList<>();
    private List<EditedBlock> changedBlock = new ArrayList<>();
    private Map<SGMap, Integer> mapVotes = new HashMap<>();
    private Map<UUID, SGMap> playerMapVotes = new HashMap<>();
    private Map<UUID, Integer> bounty = new HashMap<>();

    private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private Objective gameObjective = scoreboard.registerNewObjective("survivalgames", "dummy");

    private SGMap currentMap;

    private GameState state;

    private boolean deathmatch = false;
    private boolean isGracePeriod = false;

    private BukkitTask task;
    private BukkitTask otherTask;

    private int startCountDown = 60;
    private int deathmatchCountDown = 1800;
    private int endingOfDeathmatch = 300;
    private int gracePeriod = 15;

    public List<UUID> getInGame() {
        return this.inGame;
    }

    public List<UUID> getSpectating() {
        return this.spectating;
    }

    public List<EditedBlock> getChangedBlocks() {
        return this.changedBlock;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public boolean isDeathmatch() {
        return deathmatch;
    }

    public boolean isGracePeriod() {
        return isGracePeriod;
    }

    public Map<UUID, SGMap> getPlayerMapVotes() {
        return this.playerMapVotes;
    }

    public Map<SGMap, Integer> getMapVotes() {
        return this.mapVotes;
    }

    public Map<UUID, Integer> getBounties() {
        return this.bounty;
    }

    public Set<SGMap> getMapsInVotes() {
        return mapVotes.keySet();
    }

    public SGMap getMapInVotes(String name) {
        for(SGMap map : getMapsInVotes()) {
            if(map.getName().equalsIgnoreCase(name)) {
                return map;
            }
        }
        return null;
    }

    public void addToGame(Player player) {
        if (!inGame.contains(UUIDUtility.getUUID(player.getName()))) {
            inGame.add(UUIDUtility.getUUID(player.getName()));
        }
    }

    public void removeFromGame(Player player) {
        if(inGame.contains(UUIDUtility.getUUID(player.getName()))) {
            inGame.remove(UUIDUtility.getUUID(player.getName()));
        }
    }

    public void addToSpectator(Player player) {
        if (!spectating.contains(UUIDUtility.getUUID(player.getName()))) {
            spectating.add(UUIDUtility.getUUID(player.getName()));
        }
    }

    public void removeFromSpectator(Player player) {
        if(spectating.contains(UUIDUtility.getUUID(player.getName()))) {
            spectating.remove(UUIDUtility.getUUID(player.getName()));
        }
    }

    /*
    ############################################################
    # +------------------------------------------------------+ #
    # |               Misclleanous Game Methods              | #
    # +------------------------------------------------------+ #
    ############################################################
     */

    public void setState(GameState state) {
        this.state = state;
    }

    public GameState getState() {
        return this.state;
    }

    public Location getLobbySpawn() {
        return Main.getInstance().Data.getString("LOBBY_SPAWN.WORLD") == null ? null : LocationUtils.getWaitingLobbyLocation();
    }

    public void broadcastGame(String... messages) {
        for(String message : messages) {
            Bukkit.broadcastMessage(StringUtils.colorize(message));
        }
    }

    public void broadcastGame(MessageUtils.MessageType type, String... messages) {
        for(String message : messages) {
            Bukkit.broadcastMessage(StringUtils.colorize(type.getPrefix() + message));
        }
    }

    public SGMap getCurrentMap() {
        return this.currentMap;
    }
    
    /*
    ############################################################
    # +------------------------------------------------------+ #
    # |                   Scoreboard Handling                | #
    # +------------------------------------------------------+ #
    ############################################################
    */

    public void initScoreboardWaiting() {
        for (String score : scoreboard.getEntries()) {
            scoreboard.resetScores(score);
        }

        gameObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        gameObjective.setDisplayName(StringUtils.colorize("&eSG &7- &a&oWaiting"));

        Score s = gameObjective.getScore(StringUtils.colorize("&a&lWaiting"));
        s.setScore(5);

        Score s1 = gameObjective.getScore(Integer.toString(getInGame().size()));
        s1.setScore(4);

        Score s2 = gameObjective.getScore("     ");
        s2.setScore(3);

        Score s3 = gameObjective.getScore(StringUtils.colorize("&c&lMinimum"));
        s3.setScore(2);

        Score s4 = gameObjective.getScore(Integer.toString(Main.getInstance().Config.getInt("Minimum-Start")));
        s4.setScore(1);

        task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                boolean scoreboardReset = false;
                for (String string : scoreboard.getEntries()) {
                    if (gameObjective.getScore(string).getScore() == 4 && !string.equals(Integer.toString(getInGame().size()))) {
                        scoreboardReset = true;
                    }

                    if (scoreboardReset) {
                        for (String score : scoreboard.getEntries()) {
                            scoreboard.resetScores(score);
                        }

                        Score s = gameObjective.getScore(StringUtils.colorize("&a&lWaiting"));
                        s.setScore(5);

                        Score s1 = gameObjective.getScore(Integer.toString(getInGame().size()));
                        s1.setScore(4);

                        Score s2 = gameObjective.getScore("     ");
                        s2.setScore(3);

                        Score s3 = gameObjective.getScore(StringUtils.colorize("&c&lMinimum"));
                        s3.setScore(2);

                        Score s4 = gameObjective.getScore(Integer.toString(Main.getInstance().Config.getInt("Minimum-Start")));
                        s4.setScore(1);
                    }
                }
            }
        }, 0L, 20L);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(getScoreboard());
        }
    }

    private void initScoreboardVote() {
        for (String score : scoreboard.getEntries()) {
            scoreboard.resetScores(score);
        }

        Bukkit.getScheduler().cancelTask(task.getTaskId());

        gameObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        gameObjective.setDisplayName(StringUtils.colorize("&eSG &7- &a&oVoting"));

        for (SGMap map : SGMap.getAllMaps()) {
            Score s = gameObjective.getScore(Bukkit.getOfflinePlayer(StringUtils.colorize("&b&l" + (map.getName().length() > 16 ? map.getName().substring(0, 10) : map.getName()))));
            s.setScore(0);
        }

        /*
        Score s1 = gameObjective.getScore(StringUtils.colorize("    "));
        s1.setScore(-1);

        Score s2 = gameObjective.getScore(StringUtils.colorize("&a&lWaiting"));
        s2.setScore(-2);

        Score s3 = gameObjective.getScore(Integer.toString(getInGame().size()));
        s3.setScore(-3);

        Score s4 = gameObjective.getScore(StringUtils.colorize("    "));
        s4.setScore(-4);

        Score s5 = gameObjective.getScore(StringUtils.colorize("&c&lMinimum"));
        s5.setScore(-5);

        Score s6 = gameObjective.getScore(Integer.toString(Main.getInstance().Config.getInt("Minimum-Players")));
        s6.setScore(-6);
        */

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(getScoreboard());
        }
    }


    public void updateScoreboardVotes() {
        for(SGMap map : SGMap.getAllMaps()) {
            Score s = gameObjective.getScore(Bukkit.getOfflinePlayer(StringUtils.colorize("&b&l" + (map.getName().length() > 16 ? map.getName().substring(0, 10) : map.getName()))));
            s.setScore(mapVotes.get(getMapInVotes(map.getName())));
        }
    }

    private void initScoreboardStarting(final Player player) {
        for (String score : scoreboard.getEntries()) {
            scoreboard.resetScores(score);
        }

        gameObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        gameObjective.setDisplayName(StringUtils.colorize("&eSG &7- &a&oStarting"));

        Score s = gameObjective.getScore(StringUtils.colorize("&a&lTributes"));
        s.setScore(8);

        Score s1 = gameObjective.getScore(Integer.toString(getInGame().size()));
        s1.setScore(7);

        Score s2 = gameObjective.getScore("     ");
        s2.setScore(6);

        Score s3 = gameObjective.getScore(StringUtils.colorize("&c&lSpectators"));
        s3.setScore(5);

        Score s4 = gameObjective.getScore(Integer.toString(getSpectating().size()));
        s4.setScore(4);

        Score s5 = gameObjective.getScore("     ");
        s5.setScore(3);

        Score s6 = gameObjective.getScore(StringUtils.colorize("&6&lPoints"));
        s6.setScore(2);

        Score s7 = gameObjective.getScore(Integer.toString(DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(player.getName()))));
        s7.setScore(1);

        task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                boolean scoreboardReset = false;
                for (String string : scoreboard.getEntries()) {
                    if (gameObjective.getScore(string).getScore() == 7 && !string.equals(Integer.toString(getInGame().size()))) {
                        scoreboardReset = true;
                    } else if (gameObjective.getScore(string).getScore() == 4 && !string.equals(Integer.toString(getSpectating().size()))) {
                        scoreboardReset = true;
                    } else if (gameObjective.getScore(string).getScore() == 1 && !string.equals(Integer.toString(DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(player.getName()))))) {
                        scoreboardReset = true;
                    }

                    if (scoreboardReset) {
                        for (String score : scoreboard.getEntries()) {
                            scoreboard.resetScores(score);
                        }

                        Score s = gameObjective.getScore(StringUtils.colorize("&a&lTributes"));
                        s.setScore(8);

                        Score s1 = gameObjective.getScore(Integer.toString(getInGame().size()));
                        s1.setScore(7);

                        Score s2 = gameObjective.getScore("     ");
                        s2.setScore(6);

                        Score s3 = gameObjective.getScore(StringUtils.colorize("&c&lSpectators"));
                        s3.setScore(5);

                        Score s4 = gameObjective.getScore(Integer.toString(getSpectating().size()));
                        s4.setScore(4);

                        Score s5 = gameObjective.getScore("     ");
                        s5.setScore(3);

                        Score s6 = gameObjective.getScore(StringUtils.colorize("&6&lPoints"));
                        s6.setScore(2);

                        Score s7 = gameObjective.getScore(Integer.toString(DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(player.getName()))));
                        s7.setScore(1);
                    }
                }
            }
        }, 0L, 20L);

        for (Player players : Bukkit.getOnlinePlayers()) {
            players.setScoreboard(getScoreboard());
        }
    }

    private void initScoreboardGame(final Player player) {
        for (String score : scoreboard.getEntries()) {
            scoreboard.resetScores(score);
        }

        Bukkit.getScheduler().cancelTask(task.getTaskId());

        gameObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        gameObjective.setDisplayName(StringUtils.colorize("&eSG &7- &a&oStarting"));

        Score s = gameObjective.getScore(StringUtils.colorize("&a&lTributes"));
        s.setScore(8);

        Score s1 = gameObjective.getScore(Integer.toString(getInGame().size()));
        s1.setScore(7);

        Score s2 = gameObjective.getScore("     ");
        s2.setScore(6);

        Score s3 = gameObjective.getScore(StringUtils.colorize("&c&lSpectators"));
        s3.setScore(5);

        Score s4 = gameObjective.getScore(Integer.toString(getSpectating().size()));
        s4.setScore(4);

        Score s5 = gameObjective.getScore("     ");
        s5.setScore(3);

        Score s6 = gameObjective.getScore(StringUtils.colorize("&6&lPoints"));
        s6.setScore(2);

        Score s7 = gameObjective.getScore(Integer.toString(DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(player.getName()))));
        s7.setScore(1);

        task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                boolean scoreboardReset = false;
                for (String string : scoreboard.getEntries()) {
                    if (gameObjective.getScore(string).getScore() == 7 && !string.equals(Integer.toString(getInGame().size()))) {
                        scoreboardReset = true;
                    } else if (gameObjective.getScore(string).getScore() == 4 && !string.equals(Integer.toString(getSpectating().size()))) {
                        scoreboardReset = true;
                    } else if (gameObjective.getScore(string).getScore() == 1 && !string.equals(Integer.toString(DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(player.getName()))))) {
                        scoreboardReset = true;
                    }

                    if (scoreboardReset) {
                        for (String score : scoreboard.getEntries()) {
                            scoreboard.resetScores(score);
                        }

                        Score s = gameObjective.getScore(StringUtils.colorize("&a&lTributes"));
                        s.setScore(8);

                        Score s1 = gameObjective.getScore(Integer.toString(getInGame().size()));
                        s1.setScore(7);

                        Score s2 = gameObjective.getScore("     ");
                        s2.setScore(6);

                        Score s3 = gameObjective.getScore(StringUtils.colorize("&c&lSpectators"));
                        s3.setScore(5);

                        Score s4 = gameObjective.getScore(Integer.toString(getSpectating().size()));
                        s4.setScore(4);

                        Score s5 = gameObjective.getScore("     ");
                        s5.setScore(3);

                        Score s6 = gameObjective.getScore(StringUtils.colorize("&6&lPoints"));
                        s6.setScore(2);

                        Score s7 = gameObjective.getScore(Integer.toString(DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(player.getName()))));
                        s7.setScore(1);
                    }
                }
            }
        }, 0L, 20L);

        for (Player players : Bukkit.getOnlinePlayers()) {
            players.setScoreboard(getScoreboard());
        }
    }

        /*
    ############################################################
    # +------------------------------------------------------+ #
    # |                   Voting Handling                    | #
    # +------------------------------------------------------+ #
    ############################################################
     */


    public int getVoteValue(Player player) {
        return player.hasPermission("survivalgames.votes.member") ? Main.getInstance().getConfig().getInt("Vote-Value-Amount-Member-Rank") : player.hasPermission("survivalgames.votes.pro") ? Main.getInstance().getConfig().getInt("Vote-Value-Amount-Pro-Rank") : player.hasPermission("survivalgames.votes.elite") ? Main.getInstance().getConfig().getInt("Vote-Value-Amount-Elite-Rank") : Main.getInstance().getConfig().getInt("Vote-Value-Amount-Staff-Or-VIP-Rank");
    }

    public void startVoting() {
        initScoreboardVote();
        setState(GameState.Voting);
        broadcastGame(MessageUtils.MessageType.GOOD, "The Voting stage has started! You have 60 seconds to vote for a map to play!");

        for(Player player : Bukkit.getOnlinePlayers()) {
            SoundPlayer.play(player, Sound.NOTE_PLING, 5);
        }

        for(SGMap map : SGMap.getAllMaps()) {
            mapVotes.put(map, 0);
        }

        new BukkitRunnable() {
            int countdown = 60;
            float minusBar = 0.0F;

            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if (BossBarUtils.hasBar(player)) {
                        BossBarUtils.destroyDragon(player);
                    }

                    BossBarUtils.setBar(player, "&6itzjakeyMC.net &8» &e&lVoting &eon &e&l" + Main.getInstance().Config.getString("Server-Name"), 200.0F - minusBar);
                }

                if (countdown == 45 || countdown == 30 || countdown == 15 || (countdown <= 5 && countdown > 0)) {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(player, Sound.NOTE_PLING, 5);
                    }
                    broadcastGame(MessageUtils.MessageType.GOOD, "The Voting stage ends in &b&l" + countdown + " &aseconds!");
                }

                else if (countdown == 50 || countdown == 35 || countdown == 20 || countdown == 10) {
                    broadcastGame("&e&m----------------------------------------------------", "&6&lMap Votes", "");
                    for(SGMap map : getMapsInVotes()) {
                        broadcastGame("&b" + map.getName() + " &7» &e" + getMapVotes().get(map));
                    }
                    broadcastGame("&e&m----------------------------------------------------");
                    for(Player online : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(online, Sound.NOTE_SNARE_DRUM, 5);
                    }
                }

                if (countdown == 0) {
                    if (getInGame().size() < Main.getInstance().Config.getInt("Minimum-Players")) {
                        countdown = 60;

                        broadcastGame("&e&m----------------------------------------------------");
                        broadcastGame("&cThere are not enough players to start! &7&oResetting countdown...");
                        broadcastGame("&c&lPLAYERS NEEDED &8» &b" + Main.getInstance().Config.getInt("Minimum-Players"));
                        broadcastGame("&e&m----------------------------------------------------");
                        return;
                    }

                    this.cancel();
                    SGMap voted = getHighestVote(getMapVotes());
                    currentMap = voted;
                    broadcastGame(MessageUtils.MessageType.GOOD, "The map &b&l" + SGMap.getFriendlyMapName(currentMap.getName()) + " &aby &b&l" + voted.getAuthor() + " &ahas won voting!");
                    broadcastGame(MessageUtils.MessageType.GOOD, "&oNow Loading map &b&o" + SGMap.getFriendlyMapName(voted.getName()) + "&a&o...");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 5; i++) {
                                LoggingUtils.log(Level.SEVERE, "Current map is: " + currentMap.getName());
                            }
                        }
                    }.runTaskLater(Main.getInstance(), 20 * 3);

                    startGame();
                }

                countdown--;
                minusBar += 200.0F / 60.0F;
                minusBar = minusBar >= 200.0F ? 199.0F : minusBar;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    public SGMap getHighestVote(Map<SGMap, Integer> map) {
        SGMap highest = null;
        for(SGMap sgMap : map.keySet()) {
            if(highest == null || map.get(sgMap) > map.get(highest)) {
                highest = sgMap;
            }
        }
        return highest;
    }



    /*
    ############################################################
    # +------------------------------------------------------+ #
    # |                      Game Methods                    | #
    # +------------------------------------------------------+ #
    ############################################################
     */

    public void teleportPlayersToSpots() {
        //int count = 0;

        for(UUID player : getInGame()) {
            Player playerI = Bukkit.getPlayer(player);

            int i = Main.getInstance().rand().nextInt(SGMap.getMap("ValleysideUniversity").getSpawns().size());
            Location spawn = SGMap.getMap("ValleysideUniversity").getSpawns().get(i);
            if (spawn == null) {

            }

            playerI.teleport(spawn);

            SoundPlayer.play(playerI, Sound.FIREWORK_LARGE_BLAST, 5);
            Particle.FIREWORKS_SPARK.play(playerI.getLocation());
            playerI.getInventory().setItem(0, null);
            //count++;
        }
    }

    public void startGame() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            initScoreboardStarting(player);
        }

        setState(GameState.Starting);
        teleportPlayersToSpots();
        Main.getInstance().getConditionManager().setCanPlayersMove(false);

        new BukkitRunnable() {
            float minusBar = 0.0F;

            @Override
            public void run() {
                for (UUID pl : getInGame()) {
                    Player player = Bukkit.getPlayer(pl);

                    player.setLevel(startCountDown);
                    player.setExp(0);

                    if (BossBarUtils.hasBar(player)) {
                        BossBarUtils.destroyDragon(player);
                    }

                    BossBarUtils.setBar(player, "&6CrgSG &8» &Starting in &e&l" + startCountDown + " &e&lseconds...", 200.0F - minusBar);
                }

                if (startCountDown == 60 || startCountDown == 45 || startCountDown == 30 || startCountDown == 15 || startCountDown == 10 || (startCountDown <= 5 && startCountDown != 0)) {
                    broadcastGame(MessageUtils.MessageType.GOOD, "The game is starting in &6" + startCountDown + " &7seconds!");
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(player, Sound.NOTE_PLING, 5);
                    }
                }

                if (startCountDown == 25) {
                    broadcastGame("&e&m----------------------------------------------------", "&a&lMap Information", "");
                    broadcastGame("&bMap Name &7» &e" + getCurrentMap().getName());
                    broadcastGame("&bMap Author &7» &e" + getCurrentMap().getAuthor());
                    broadcastGame("&bLink &7» &e" + getCurrentMap().getLink());
                    broadcastGame("&e&m----------------------------------------------------");
                    for(Player online : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(online, Sound.NOTE_SNARE_DRUM, 5);
                    }
                }

                if (startCountDown == 15) {
                    broadcastGame("&e&m----------------------------------------------------", "&a&lSurvival Games", "");
                    broadcastGame("&7» &eLoot the chests found all over the map!");
                    broadcastGame("&7» &eKill all the tributes to be the last one remaining!");
                    broadcastGame("&7» &eThe deathmatch will begin in &630 &eminutes!");
                    broadcastGame("&e&m----------------------------------------------------");

                    for(Player online : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(online, Sound.NOTE_SNARE_DRUM, 5);
                    }
                }

                else if (startCountDown == 0) {
                    this.cancel();
                    Main.getInstance().getConditionManager().setCanPlayersMove(true);

                    for (UUID uuid : getInGame()) {
                        Player player = Bukkit.getPlayer(uuid);

                        player.getInventory().clear();
                        player.getInventory().setArmorContents(null);
                        player.setGameMode(GameMode.SURVIVAL);
                        player.setFireTicks(0);
                        player.setHealth(player.getMaxHealth());
                        player.setSaturation(5F);
                        player.setFoodLevel(20);
                        player.setAllowFlight(false);
                        player.setFlying(false);

                        initScoreboardGame(player);

                        SoundPlayer.play(player, Sound.WITHER_SPAWN, 3);
                        SoundPlayer.play(player, Sound.LEVEL_UP, 5);

                        if (BossBarUtils.hasBar(player)) {
                            BossBarUtils.destroyDragon(player);
                        }

                        BossBarUtils.setBar(player, "&6CrgSG &8» &ePlaying &e&l" + getCurrentMap().getName() + " &eon &e&l" + Main.getInstance().Config.getInt("Server-Name"), 200.0F);
                    }

                    setState(GameState.In_Game);
                    broadcastGame(MessageUtils.MessageType.GOOD, "The games have begun! May the odds be ever in your favor!");

                    startGracePeriod();
                    isGracePeriod = true;

                    countdownToDeathmatch();
                }

                startCountDown--;
                minusBar += 200.0F / 60.0F;
                minusBar = minusBar >= 200.0F ? 199.0F : minusBar;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    public void startGracePeriod() {
        new BukkitRunnable() {
            float minusBar = 0.0F;

            @Override
            public void run() {
                for(UUID uuid : getInGame()) {
                    Player player = Bukkit.getPlayer(uuid);

                    player.setLevel(gracePeriod);
                    player.setExp(0);

                    if (BossBarUtils.hasBar(player)) {
                        BossBarUtils.destroyDragon(player);
                    }

                    BossBarUtils.setBar(player, "&6CrgSG &8» &ePlaying &e&l" + getCurrentMap().getName() + " &eon &e&l" + Main.getInstance().Config.getInt("Server-Name"), 200.0F - minusBar);
                }

                if (gracePeriod == 15) {
                    broadcastGame(MessageUtils.MessageType.GOOD, "You now have a grace period lasting &615 &7seconds!");
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(player, Sound.NOTE_PLING, 5);
                    }
                }

                if (gracePeriod == 10) {
                    broadcastGame(MessageUtils.MessageType.GOOD, "There are &6" + gracePeriod + " &7seconds left of grace period!");
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(player, Sound.NOTE_PLING, 5);
                    }
                }

                else if (gracePeriod == 0) {
                    this.cancel();
                    isGracePeriod = false;
                    broadcastGame(MessageUtils.MessageType.GOOD, "The grace period has now expired!");

                    for(UUID uuid : getInGame()) {
                        Player player = Bukkit.getPlayer(uuid);

                        SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 5);

                        if (BossBarUtils.hasBar(player)) {
                            BossBarUtils.destroyDragon(player);
                        }

                        BossBarUtils.setBar(player, "&6CrgSG &8» &ePlaying &e&l" + getCurrentMap().getName() + " &eon &e&l" + Main.getInstance().Config.getInt("Server-Name"), 200.0F);
                    }
                }

                gracePeriod--;
                minusBar += 200.0F / 15.0F;
                minusBar = minusBar >= 200.0F ? 199.0F : minusBar;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

        /*
    ############################################################
    # +------------------------------------------------------+ #
    # |                  Deathmatch Methods                  | #
    # +------------------------------------------------------+ #
    ############################################################
     */

    public void teleportPlayersToDeathmatchSpawns() {
        int count = 0;
        for(UUID player : getInGame()) {
            Player playerI = Bukkit.getPlayer(player);
            playerI.teleport(LocationUtils.getDeathmatchSpawns().get(count));
            SoundPlayer.play(playerI, Sound.FIREWORK_LARGE_BLAST, 5);
            Particle.FIREWORKS_SPARK.play(playerI.getLocation());
            count++;
        }
    }

    public void countdownToDeathmatch() {
        new BukkitRunnable() {
            float minusBar = 0.0F;

            @Override
            public void run() {
                if (deathmatchCountDown == 1800) {
                    broadcastGame(MessageUtils.MessageType.GOOD, "&lAll players will receive a player tracker! Kill your foes!");

                    for (int i = 0; i < Game.getInstance().getInGame().size(); i++) {
                        Player player = Bukkit.getPlayer(getInGame().get(i));
                        player.getInventory().setItem(8, ItemUtils.createItemStack("&c&lTribute Tracker &7(Right Click)", Arrays.asList("&7Right click to track a tribute!"), Material.COMPASS));
                    }

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(player, Sound.LEVEL_UP, 5);
                    }
                }

                if (deathmatchCountDown == 1500 || deathmatchCountDown == 900 || deathmatchCountDown == 540 || deathmatchCountDown == 300) {
                    broadcastGame(MessageUtils.MessageType.GOOD, "The deathmatch will start in &b&l" + TimeUtil.formatTime(deathmatchCountDown) + "&a!");
                    broadcastGame(MessageUtils.MessageType.GOOD, "There are only &b&l" + getInGame().size() + " &atributes remaining.");
                    broadcastGame(MessageUtils.MessageType.GOOD, "There are &b&l" + getSpectating().size() + " &aspectators watching.");

                    for(Player player : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(player, Sound.NOTE_PLING, 5);
                    }
                }

                if (deathmatchCountDown == 600) {
                    broadcastGame(MessageUtils.MessageType.GOOD, "&lAll chests have been refilled! Start looting tributes!");
                    ChestManager.getChests().clear();

                    for(Player player : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(player, Sound.ANVIL_BREAK, 5);
                    }
                }


                if (deathmatchCountDown <= 120) {
                    broadcastGame(MessageUtils.MessageType.GOOD, "&lLightning will now strike at all tributes' locations helping to hunt them faster! Good luck!");

                    otherTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            for (UUID uuid : getInGame()) {
                                Player player = Bukkit.getPlayer(uuid);
                                player.getWorld().strikeLightningEffect(player.getLocation());
                                player.getWorld().playSound(player.getWorld().getSpawnLocation(), Sound.AMBIENCE_THUNDER, 10000, 2.9F);
                            }
                        }
                    }, 0L, 20L * 5L);
                }

                if (deathmatchCountDown == 60 || deathmatchCountDown == 30 || deathmatchCountDown == 10 || (deathmatchCountDown <= 5 && deathmatchCountDown != 0)) {
                    broadcastGame(MessageUtils.MessageType.GOOD, "The deathmatch will start in &6" + TimeUtil.formatTime(deathmatchCountDown) + "&7!");

                    for(Player player : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(player, Sound.NOTE_PLING, 5);
                    }

                    for (UUID uuid : getInGame()) {
                        Player player = Bukkit.getPlayer(uuid);

                        if (BossBarUtils.hasBar(player)) {
                            BossBarUtils.destroyDragon(player);
                        }

                        BossBarUtils.setBar(player, "&6CrgSG &8» &eDeathmatch starting in &e&l" + deathmatchCountDown + " &7&lseconds...", 200);
                    }
                }

                else if (deathmatchCountDown == 0) {
                    this.cancel();
                    Bukkit.getScheduler().cancelTask(otherTask.getTaskId());

                    setState(GameState.Deathmatch);
                    teleportPlayersToDeathmatchSpawns();
                    deathmatch = true;

                    broadcastGame("&e&m----------------------------------------------------", "&a&lDeathmatch", "");
                    broadcastGame("&7» &eThe deathmatch has now begun!");
                    broadcastGame("&7» &eThe last remaining tribute will win the game!");
                    broadcastGame("&7» &eThere are &b5 &eminutes remaining until the deathmatch concludes.");
                    broadcastGame("&e&m----------------------------------------------------");

                    endingToDeathmatch();

                    broadcastGame(MessageUtils.MessageType.GOOD, "&lAll chests have been refilled!");
                    ChestManager.getChests().clear();

                    for(Player player : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(player, Sound.ANVIL_BREAK, 5);
                    }
                }

                deathmatchCountDown--;
                minusBar += 200.0F / 60.0F;
                minusBar = minusBar >= 200.0F ? 199.0F : minusBar;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    public void endingToDeathmatch() {
        new BukkitRunnable() {
            float minusBar = 0.0F;

            @Override
            public void run() {
                for (UUID uuid : getInGame()) {
                    Player player = Bukkit.getPlayer(uuid);

                    if (BossBarUtils.hasBar(player)) {
                        BossBarUtils.destroyDragon(player);
                    }

                    BossBarUtils.setBar(player, "&6CrgSG &8» &eDeathmatch ending in &e&l" + TimeUtil.formatTime(endingOfDeathmatch) + " &e!", 200.0F - minusBar);
                }

                if (endingOfDeathmatch == 240) {
                    broadcastGame(MessageUtils.MessageType.GOOD, "There are &b4 &aminutes remaining until the deathmatch ends!");

                    for(Player player : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 5);
                    }
                }

                if (endingOfDeathmatch == 180) {
                    broadcastGame(MessageUtils.MessageType.GOOD, "There are &b3 &aminutes remaining until the deathmatch ends!");

                    for(Player player : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 5);
                    }
                }

                if (endingOfDeathmatch == 120) {
                    broadcastGame(MessageUtils.MessageType.GOOD, "There are &b2 &aminutes remaining until the deathmatch ends!");

                    for(Player player : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 5);
                    }
                }

                if(endingOfDeathmatch == 60 || endingOfDeathmatch == 45 || endingOfDeathmatch == 30 || endingOfDeathmatch == 15 || endingOfDeathmatch == 10 || (endingOfDeathmatch <= 5 && endingOfDeathmatch != 0)) {
                    broadcastGame(MessageUtils.MessageType.GOOD, "There are &b" + endingOfDeathmatch + " &aseconds until the deathmatch ends!");

                    for(Player player : Bukkit.getOnlinePlayers()) {
                        SoundPlayer.play(player, Sound.NOTE_SNARE_DRUM, 5);
                    }
                }

                if (endingOfDeathmatch == 0) {
                    endGame(false);
                }

                endingOfDeathmatch--;
                minusBar += 200.0F / 300.0F;
                minusBar = minusBar >= 200.0F ? 199.0F : minusBar;

            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    /*
    ############################################################
    # +------------------------------------------------------+ #
    # |                  Core Game Handling                  | #
    # +------------------------------------------------------+ #
    ############################################################
     */

    public void killPlayer(Player player) {
        removeFromGame(player);
        addToSpectator(player);

        broadcastGame(MessageUtils.MessageType.GOOD, "Tribute &b&l" + player.getName() + " &ahas been killed!");
        broadcastGame(MessageUtils.MessageType.GOOD, "Only &b&l" + getInGame().size() + " &atributes remain.");

        for (ItemStack i : player.getInventory().getContents()) {
            if (i != null) {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), i);
            }
        }

        for (ItemStack i : player.getInventory().getArmorContents()) {
            if ((i != null) && (i.getType() != Material.AIR)) {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), i);
            }
        }

        player.getWorld().strikeLightningEffect(player.getLocation());
        player.getWorld().playSound(player.getWorld().getSpawnLocation(), Sound.AMBIENCE_THUNDER, 10000, 2.9F);
    }

    public void endGame(final boolean winner) {
        if(winner) {
            final Player winnerPlayer = Bukkit.getPlayer(Game.getInstance().getInGame().get(0));
            DatabaseManager.getInstance().addWin(UUIDUtility.getUUID(winnerPlayer.getName()));
            DatabaseManager.getInstance().setPoints(UUIDUtility.getUUID(winnerPlayer.getName()), DatabaseManager.getInstance().getPoints(UUIDUtility.getUUID(winnerPlayer.getName())) + 100);
            UCoinAPI.giveCoins(UUIDUtility.getUUID(winnerPlayer.getName()), Main.getInstance().Config.getInt("Winning-Coin-Amount"));

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (BossBarUtils.hasBar(player)) {
                    BossBarUtils.destroyDragon(player);
                }

                BossBarUtils.setBar(player, "&b&l" + winnerPlayer.getName() + " &a&lhas won the Survival Games!", 200);
            }

            broadcastGame("&e&m----------------------------------------------------", "&a&lSurvival Games", "");
            broadcastGame("&7» &eThe Survival Games are now over!");
            broadcastGame("&7» &c&oThis server will be restarting in 5 seconds...");
            broadcastGame(" ");
            broadcastGame("&b&lWINNER TRIBUTE:");
            broadcastGame("&7» &b&l" + winnerPlayer.getName());
            broadcastGame("&e&m----------------------------------------------------");

            new BukkitRunnable() {
                int cd = 10;
                @Override
                public void run() {
                    FireworkEffectPlayer.playToLocation(winnerPlayer.getLocation(), FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(Color.fromRGB(Main.getInstance().rand().nextInt(255), Main.getInstance().rand().nextInt(255), Main.getInstance().rand().nextInt(255))).withFlicker().build());

                    if(cd == 0) {
                        this.cancel();
                    }
                    cd--;
                }
            }.runTaskTimer(Main.getInstance(), 0, 20);
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (BossBarUtils.hasBar(player)) {
                    BossBarUtils.destroyDragon(player);
                }

                BossBarUtils.setBar(player, "&6&lThe Survival Games have ended! &c&lNobody won!", 200);
            }

            broadcastGame("&e&m----------------------------------------------------", "&a&lSurvival Games", "");
            broadcastGame("&7» &eThe Survival Games have now ended!");
            broadcastGame(" ");
            broadcastGame("&c&lNobody won this round!");
            broadcastGame("&c&lThis server will be restarting in 5 seconds...");
            broadcastGame("&e&m----------------------------------------------------");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Main.getBungeeManager().sendToServer(player, Main.getInstance().Config.getString("Main-Hub-Server-Name"));
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().shutdown();
                    }
                }.runTaskLater(Main.getInstance(), 20 * 5);
            }
        }.runTaskLater(Main.getInstance(), 20 * 10);
    }

}
