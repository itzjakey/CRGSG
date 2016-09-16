package li.itzjakey.CrgSG.API;

import io.anw.Core.Bukkit.AuroraPlugin;
import li.itzjakey.CrgSG.API.Commands.ListPlayers;
import li.itzjakey.CrgSG.API.Utils.Conditions;
import li.itzjakey.CrgSG.API.Utils.GameUtils;

public abstract class GamePlugin extends AuroraPlugin {

    private GameUtils gameUtils;
    private Conditions conditionManager = new Conditions(this);

    public GameUtils getGameUtils() {
        return this.gameUtils;
    }

    public void setGameUtils(GameUtils utils) {
        this.gameUtils = utils;
    }

    public Conditions getConditionManager() {
        return this.conditionManager;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        addListener(getConditionManager());
        registerListeners();

        addCommandClass(ListPlayers.class);
        registerCommands();
    }

}
