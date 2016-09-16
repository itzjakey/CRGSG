package li.itzjakey.CrgSG;

import io.anw.Core.Bukkit.Utils.Bungee.BungeeManager;
import io.anw.Core.Bukkit.Utils.Bungee.BungeeRequestManager;
import li.itzjakey.CrgSG.API.GamePlugin;
import li.itzjakey.CrgSG.Chest.ChestManager;
import li.itzjakey.CrgSG.Commands.Commands;
import li.itzjakey.CrgSG.Game.Game;
import li.itzjakey.CrgSG.Game.GameState;
import li.itzjakey.CrgSG.Listeners.*;
import li.itzjakey.CrgSG.Listeners.Checks.ChatChecks;
import li.itzjakey.CrgSG.Listeners.Checks.GeneralChecks;
import li.itzjakey.CrgSG.Utils.DatabaseManager;
import li.itzjakey.CrgSG.Utils.GameUtils;
import li.itzjakey.CrgSG.Utils.LoggingUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;

import java.io.File;

public class Main extends GamePlugin {

    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    private static BungeeManager bungeeManager;
    public static BungeeManager getBungeeManager() {
        return bungeeManager;
    }

    public File configFile;
    public FileConfiguration Config;
    public File dataFile;
    public FileConfiguration Data;
    public File chestFile;
    public FileConfiguration Chest;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        bungeeManager = new BungeeRequestManager(this);

        configFile = new File(getDataFolder(), "config.yml");
        dataFile = new File(getDataFolder(), "data.yml");
        chestFile = new File(getDataFolder(), "chest.yml");
        getConfigManager().addFile(configFile);
        getConfigManager().addFile(dataFile);
        getConfigManager().addFile(chestFile);
        try {
            getConfigManager().firstRun();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Config = getConfigManager().getConfigFile("config.yml");
        Data = getConfigManager().getConfigFile("data.yml");
        Chest = getConfigManager().getConfigFile("chest.yml");
        getConfigManager().load();
        getConfigManager().save();

        setGameUtils(new GameUtils());
        getGameUtils().setPrefix("&8[&eCrgSG&8]:");

        Game.getInstance().setState(GameState.Waiting);

        DatabaseManager.getInstance().checkDatabase();

        addListener(new ChatChecks());
        addListener(new GeneralChecks());

        addListener(new Join());
        addListener(new Leave());

        addListener(new LobbyItems());
        addListener(new GameItems());

        addListener(new ChestManager());

        addListener(new DamageHandler());
        registerListeners();

        addCommandClass(Commands.class);
        registerCommands();

        Game.getInstance().initScoreboardWaiting();

        for (World world : Bukkit.getWorlds()) {
            world.setTime(1000L);
        }
    }

    @Override
    public void onDisable() {
        Game.getInstance().setState(GameState.Restarting);

        for (Entity e : Game.getInstance().getCurrentMap().getWorld().getEntities()) {
            e.remove();
            LoggingUtils.log("Cleared all entities for map " + Game.getInstance().getCurrentMap().getName() + " in world " + Game.getInstance().getCurrentMap().getWorld().getName() + "!");
        }
    }

}
