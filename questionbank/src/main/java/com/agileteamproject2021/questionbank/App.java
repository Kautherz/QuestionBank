package com.agileteamproject2021.questionbank;

public final class App {
	private static AppLogger logger;
    private static DataFile dataFile;

    /**
     * Entrypoint of the program
     * 
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        // instantiate logger
        AppLogger logger = AppLogger.getInstance();
        logger.logInfo("Hello World!");

        // setup the datafile
        // this is done here to instantiate the datafile and
        // catch any exception before moving into the app
        try {
            DataFile dataFile = new DataFile();
        }
        catch (InvalidXmlException e) {
            logger.logSevere("Invalid XML was parsed. Check that the XML file has the expected structures or delete it. Exiting program.");
            return;
        }

        // create the window
        new MainWindow();

    }
}
