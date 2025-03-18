package se.miun.dt133g.zkgitclient.logger;

import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import java.util.logging.Formatter;

public final class ZkGitLogger {

    private static final ConsoleHandler consoleHandler = new ConsoleHandler();

    static {
        try {
            consoleHandler.setFormatter(new ColoredFormatter());
            consoleHandler.setLevel(AppConfig.LOG_LEVEL);

            Logger rootLogger = Logger.getLogger(ZkGitLogger.class.getName());
            rootLogger.addHandler(consoleHandler);
            rootLogger.setLevel(AppConfig.LOG_LEVEL);
            rootLogger.setUseParentHandlers(false);

        } catch (Exception e) {
            System.err.println("Logging initialization failed: " + e.getMessage());
        }
    }

    public static Logger getLogger(Class<?>  clazz) {
        Logger logger = Logger.getLogger(clazz.getName());
        
        logger.setLevel(AppConfig.LOG_LEVEL);
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);
        
        return logger;
    }

    private static class ColoredFormatter extends Formatter {
        private static final String RESET = "\u001B[0m"; // Reset color
        private static final String RED = "\u001B[31m";  // SEVERE (Red)
        private static final String YELLOW = "\u001B[33m"; // WARNING (Yellow)
        private static final String BLUE = "\u001B[34m"; // INFO (Blue)
        private static final String GREEN = "\u001B[32m"; // CONFIG (Green)
        private static final String CYAN = "\u001B[36m"; // FINE (Cyan)
        private static final String PURPLE = "\u001B[35m"; // FINER (Purple)
        private static final String MAGENTA = "\u001B[95m";

        @Override
        public String format(LogRecord record) {
            String color;
            Level level = record.getLevel();

            if (level == Level.SEVERE) {
                color = RED;
            } else if (level == Level.WARNING) {
                color = YELLOW;
            } else if (level == Level.INFO) {
                color = BLUE;
            } else if (level == Level.CONFIG) {
                color = CYAN;
            } else if (level == Level.FINE) {
                color = GREEN;
            } else if (level == Level.FINER) {
                color = PURPLE;
            } else if (level == Level.FINEST) {
                color = MAGENTA;
            } else {
                color = RESET; // Default color
            }

            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(record.getMillis()));

            return String.format("%s[%s] [%s] %s%n[%s]: %s%s%n",
                                 color,
                                 timestamp,
                                 record.getLoggerName(),
                                 record.getSourceMethodName(),
                                 record.getLevel(),
                                 record.getMessage(),
                                 RESET
                                 );
        }
    }
}
