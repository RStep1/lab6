package commands;

import processing.BufferedDataBase;
import mods.ExecuteMode;

/**
 * Acts as a wrapper for the 'info' command.
 * Calls the method containing the implementation of this command.
 */
public class InfoCommand implements Command {
    private BufferedDataBase bufferedDataBase;
    private static final String NAME = "info";
    private static final String ARGUMENTS = "";
    private static final String DESCRIPTION = "displays information about the collection " +
            "(type, initialization date, number of elements, last saved date))";
    private static final int COUNT_OF_EXTRA_ARGUMENTS = 0;
    public InfoCommand(BufferedDataBase bufferedDataBase) {
        this.bufferedDataBase = bufferedDataBase;
    }

    @Override
    public boolean execute(String[] arguments, String[] vehicleValues, ExecuteMode executeMode) {
        return bufferedDataBase.info(arguments, vehicleValues, executeMode);
    }

    public static String getName() {
        return NAME;
    }

    public static String getDescription() {
        return DESCRIPTION;
    }

    public static int getCountOfExtraArguments() {
        return COUNT_OF_EXTRA_ARGUMENTS;
    }

    @Override
    public String toString() {
        return NAME + ARGUMENTS + ": " + DESCRIPTION;
    }
}
