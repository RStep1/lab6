package utility;

import mods.ExecuteMode;

import java.util.Arrays;

public class CommandArguments {
    private final String[] arguments;
    private final String[] extraArguments;
    private final ExecuteMode executeMode;

    public CommandArguments(String[] arguments, String[] extraArguments, ExecuteMode executeMode) {
        this.arguments = arguments;
        this.extraArguments = extraArguments;
        this.executeMode = executeMode;
    }

    public String getCommandName() {
        return arguments[0];
    }

    public String[] getArguments() {
        return arguments;
    }

    public String[] getExtraArguments() {
        return extraArguments;
    }

    public ExecuteMode getExecuteMode() {
        return executeMode;
    }

    @Override
    public String toString() {
        return String.format(
                """
                Command name: %s
                Argument: %s
                Vehicle arguments: %s
                Execute mode: %s
                """,
                arguments[0], Arrays.toString(arguments),
                Arrays.toString(extraArguments), executeMode);
    }
}
