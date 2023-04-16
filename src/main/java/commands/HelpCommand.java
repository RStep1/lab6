package commands;
import processing.BufferedDataBase;
import mods.ExecuteMode;
import utility.CommandArguments;

/**
 * Acts as a wrapper for the 'help' command.
 * Calls the method containing the implementation of this command.
 */
public class HelpCommand implements Command {
    private BufferedDataBase dataBase;
    private static final String NAME = "help";
    private static final String ARGUMENTS = "";
    private static final String DESCRIPTION = "displays help for available commands";
    private static final int COUNT_OF_EXTRA_ARGUMENTS = 0;
    public HelpCommand(BufferedDataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public boolean execute(CommandArguments commandArguments) {
        return dataBase.help(commandArguments);
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
