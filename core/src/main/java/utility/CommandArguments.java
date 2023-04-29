package utility;

import mods.ExecuteMode;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Arrays;

public record CommandArguments(String commandName, String[] arguments, String[] extraArguments,
                               ExecuteMode executeMode) implements Serializable {

    @Override
    public String toString() {
        return String.format(
                """
                        Command name: %s
                        Argument: %s
                        Vehicle arguments: %s
                        Execute mode: %s
                        """,
                commandName, Arrays.toString(arguments),
                Arrays.toString(extraArguments), executeMode);
    }
}
