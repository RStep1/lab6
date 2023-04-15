package commands;

import processing.BufferedDataBase;
import mods.ExecuteMode;


/**
 * Acts as a wrapper for the 'execute script' command.
 * Calls the method containing the implementation of this command.
 */
public class ExecuteScriptCommand implements Command {
    private BufferedDataBase bufferedDataBase;
    private static final String NAME = "execute_script";
    private static final String ARGUMENTS = " <file_name>";
    private static final String DESCRIPTION =
            "reads and executes a script from the specified file. The script contains commands" +
                    " in the same form in which they are entered by the user in interactive mode.";
    private static final int COUNT_OF_EXTRA_ARGUMENTS = 0;
    public ExecuteScriptCommand(BufferedDataBase bufferedDataBase) {
        this.bufferedDataBase = bufferedDataBase;
    }

    @Override
    public boolean execute(String[] arguments, String[] vehicleValues, ExecuteMode executeMode) {
        return bufferedDataBase.executeScript(arguments, vehicleValues, executeMode);
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
