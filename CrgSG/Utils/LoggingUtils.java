package li.itzjakey.CrgSG.Utils;

import li.itzjakey.CrgSG.Main;

import java.util.logging.Level;

public class LoggingUtils {

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static void log(Level level, String message) {
        Main.getInstance().getLogger().log(level, message);
    }

}
