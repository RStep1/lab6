package commands;

import processing.BufferedDataBase;
import utility.CommandArguments;

/**
 * Acts as a wrapper for the 'remove key' command.
 * Calls the method containing the implementation of this command.
 */
public class RemoveKeyCommand implements Command {
    private BufferedDataBase bufferedDataBase;
    private static final String NAME = "remove_key";
    private static final String ARGUMENTS = " <key>";
    private static final String DESCRIPTION = "removes an element from the collection by its key";
    private static final int COUNT_OF_EXTRA_ARGUMENTS = 0;

    public RemoveKeyCommand(BufferedDataBase bufferedDataBase) {
        this.bufferedDataBase = bufferedDataBase;
    }

    @Override
    public boolean execute(CommandArguments commandArguments) {
        return bufferedDataBase.removeKey(commandArguments);
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
