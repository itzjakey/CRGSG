package li.itzjakey.CrgSG.Utils;

import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static String formatTime(int seconds) {
        int milliseconds = seconds * 1000;
        long minute = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long sec = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));

        return String.format("%dm%ds", minute, sec);
    }

}
