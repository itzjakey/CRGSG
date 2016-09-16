package li.itzjakey.CrgSG.API.Utils;

import li.itzjakey.CrgSG.Main;

public class RandomUtils {

    public static int getRandom(int lower, int upper) {
        return Main.getInstance().rand().nextInt(upper - lower) + lower;
    }

}
