package utility;

import mods.ExecuteMode;

import java.util.Arrays;

public record CommandArguments(String commandName, String[] arguments, String[] extraArguments,
                               ExecuteMode executeMode) {

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
