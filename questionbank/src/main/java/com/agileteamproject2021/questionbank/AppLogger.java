package com.agileteamproject2021.questionbank;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AppLogger {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static AppLogger appLogger;

    public static AppLogger getInstance() {
        if (appLogger == null) {
            appLogger = new AppLogger();
        }
        return appLogger;
    }

    /**
     * Log an info statement.
     * 
     * @param message information to be logged
     */
    public void logInfo(String message) {
        LOGGER.log(Level.INFO, message);
    }

    /**
     * Log a severe statement.
     * 
     * @param message information to be logged
     */
    public void logSevere(String message) {
        LOGGER.log(Level.SEVERE, message);
    }
}
