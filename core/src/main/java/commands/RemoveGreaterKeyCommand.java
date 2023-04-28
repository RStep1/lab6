package commands;

import processing.BufferedDataBase;
import utility.CommandArguments;

/**
 * Acts as a wrapper for the 'remove greater key' command.
 * Calls the method containing the implementation of this command.
 */
public class RemoveGreaterKeyCommand implements Command {
    private BufferedDataBase bufferedDataBase;
    private static final String NAME = "remove_greater_key";
    private static final String ARGUMENTS = " <key>";
    private static final String DESCRIPTION =
            "removes from the collection all elements whose key is greater than the specified";
    private static final int COUNT_OF_ARGUMENTS = 1;
    private static final int COUNT_OF_EXTRA_ARGUMENTS = 0;

    public RemoveGreaterKeyCommand(BufferedDataBase bufferedDataBase) {
        this.bufferedDataBase = bufferedDataBase;
    }

    @Override
    public boolean execute(CommandArguments commandArguments) {
        return bufferedDataBase.removeGreaterKey(commandArguments);
    }

    public static String getName() {
        return NAME;
    }

    public static String getDescription() {
        return DESCRIPTION;
    }

    public static int getCountOfArguments() {
        return COUNT_OF_ARGUMENTS;
    }

    public static int getCountOfExtraArguments() {
        return COUNT_OF_EXTRA_ARGUMENTS;
    }

    @Override
    public String toString() {
        return NAME + ARGUMENTS + ": " + DESCRIPTION;
    }
}