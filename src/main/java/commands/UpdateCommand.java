package commands;

import data.Vehicle;
import processing.BufferedDataBase;
import mods.ExecuteMode;
import utility.CommandArguments;

/**
 * Acts as a wrapper for the 'update' command.
 * Calls the method containing the implementation of this command.
 */
public class UpdateCommand implements Command {
    private BufferedDataBase bufferedDataBase;
    private static final String NAME = "update";
    private static final String ARGUMENTS = " <id> <element>";
    private static final String DESCRIPTION =
            "updates the value of the collection element whose id is equal to the given one";
    private static final int COUNT_OF_EXTRA_ARGUMENTS = Vehicle.getCountOfChangeableFields();

    public UpdateCommand(BufferedDataBase bufferedDataBase) {
        this.bufferedDataBase = bufferedDataBase;
    }

    @Override
    public boolean execute(CommandArguments commandArguments) {
        return bufferedDataBase.update(commandArguments);
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
