package commands;

import utility.CommandArguments;

/**
 * Interface for all commands.
 * Execute method passes the command arguments and the mode in which the command is executed.
 */
public interface Command {
//    boolean execute(String[] arguments, String[] extraArguments, ExecuteMode executeMode);
    boolean execute(CommandArguments commandArguments);
}
