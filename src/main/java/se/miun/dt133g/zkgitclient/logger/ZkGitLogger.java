package se.miun.dt133g.zkgitclient.logger;

import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import java.util.logging.Formatter;

/**
 * Utility class to initialize and provide a custom logger for the application.
 * The logger supports colorized output based on log levels for better readability.
 * @author Leif Rogell
 */
public final class ZkGitLogger {

    private static final ConsoleHandler CONSOLE_HANDLER = new ConsoleHandler();

    /**
     * Private constructor to prevent instantiation.
     */
    private ZkGitLogger() { }

    static {
        try {
            CONSOLE_HANDLER.setFormatter(new ColoredFormatter());
            CONSOLE_HANDLER.setLevel(AppConfig.LOG_LEVEL);

            Logger rootLogger = Logger.getLogger(ZkGitLogger.class.getName());
            rootLogger.addHandler(CONSOLE_HANDLER);
            rootLogger.setLevel(AppConfig.LOG_LEVEL);
            rootLogger.setUseParentHandlers(false);

        } catch (Exception e) {
            System.err.println("Logging initialization failed: " + e.getMessage());
        }
    }

    /**
     * Returns a logger instance for the specified class with the application-defined logging level.
     * The logger uses a colored formatter for better log readability.
     * @param clazz the class for which the logger is created
     * @return a logger instance for the specified class
     */
    public static Logger getLogger(final Class<?>  clazz) {
        Logger logger = Logger.getLogger(clazz.getName());

        logger.setLevel(AppConfig.LOG_LEVEL);
        logger.setUseParentHandlers(false);

        if (logger.getHandlers().length == 0) {
            logger.addHandler(CONSOLE_HANDLER);
        }

        return logger;
    }

    /**
     * Custom formatter class to apply colored output based on the log level.
     */
    private static final class ColoredFormatter extends Formatter {
        private static final String RESET = "\u001B[0m";
        private static final String RED = "\u001B[31m";
        private static final String YELLOW = "\u001B[33m";
        private static final String BLUE = "\u001B[34m";
        private static final String GREEN = "\u001B[32m";
        private static final String CYAN = "\u001B[36m";
        private static final String PURPLE = "\u001B[35m";
        private static final String MAGENTA = "\u001B[95m";

        /**
         * Formats the log record by adding colors based on log level and including a timestamp.
         * @param record the log record to format
         * @return a string representing the formatted log record
         */
        @Override
        public String format(final LogRecord record) {
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
                color = RESET;
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
