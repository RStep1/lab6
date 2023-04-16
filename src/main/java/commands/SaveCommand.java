package commands;

import processing.BufferedDataBase;
import mods.ExecuteMode;
import utility.CommandArguments;

/**
 * Acts as a wrapper for the 'save' command.
 * Calls the method containing the implementation of this command.
 */
public class SaveCommand implements Command {
    private BufferedDataBase bufferedDataBase;
    private static final String NAME = "save";
    private static final String ARGUMENTS = "";
    private static final String DESCRIPTION = "save the collection to file";
    private static final int COUNT_OF_EXTRA_ARGUMENTS = 0;

    public SaveCommand(BufferedDataBase bufferedDataBase) {
        this.bufferedDataBase = bufferedDataBase;
    }

    @Override
    public boolean execute(CommandArguments commandArguments) {
        return bufferedDataBase.save(commandArguments);
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
