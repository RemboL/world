package pl.rembol.jme3.rts.gameobjects.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

class LogLine {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ");

    private final Date time;

    private final String message;

    LogLine(String message) {
        this.message = message;
        this.time = new Date();
    }

    String getMessage() {
        return SDF.format(time) + message;
    }

    public static int compare(LogLine logLine1, LogLine logLine2) {
        return logLine1.time.compareTo(logLine2.time);
    }
}
