package commands;

import processing.BufferedDataBase;
import utility.CommandArguments;

/**
 * Acts as a wrapper for the 'show' command.
 * Calls the method containing the implementation of this command.
 */
public class ShowCommand implements Command {
    private BufferedDataBase bufferedDataBase;
    private static final String NAME = "show";
    private static final String ARGUMENTS = "";
    private static final String DESCRIPTION =
            "prints to standard output all elements of the collection in string representation";
    private static final int COUNT_OF_ARGUMENTS = 0;
    private static final int COUNT_OF_EXTRA_ARGUMENTS = 0;

    public ShowCommand(BufferedDataBase bufferedDataBase) {
        this.bufferedDataBase = bufferedDataBase;
    }

    @Override
    public boolean execute(CommandArguments commandArguments) {
        return bufferedDataBase.show(commandArguments);
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
