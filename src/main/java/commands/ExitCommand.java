package commands;
import processing.BufferedDataBase;
import mods.ExecuteMode;

/**
 * Acts as a wrapper for the 'exit' command.
 * Calls the method containing the implementation of this command.
 */
public class ExitCommand implements Command {
    private BufferedDataBase dataBase;
    private static final String NAME = "exit";
    private static final String ARGUMENTS = "";
    private static final String DESCRIPTION =
            "terminates the program without saving to a file";
    private static final int COUNT_OF_EXTRA_ARGUMENTS = 0;
    public ExitCommand(BufferedDataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public boolean execute(String[] arguments, String[] vehicleValues, ExecuteMode executeMode) {
        return dataBase.exit(arguments, vehicleValues, executeMode);
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
    }}
