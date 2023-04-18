package processing;

import commands.*;
import utility.CommandArguments;
import utility.FileHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Calls each wrapper command.
 */
public class CommandInvoker {
    private final Map<String, Command> commandMap = new HashMap<>();

    /**
     * Initializes each command and set reference file.
     */
    public CommandInvoker(Command helpCommand, Command infoCommand, Command showCommand,
                          Command insertCommand, Command updateCommand, Command removeKeyCommand,
                          Command clearCommand, Command saveCommand, Command executeScriptCommand,
                          Command exitCommand, Command removeGreaterCommand, Command removeLowerCommand,
                          Command removeGreaterKeyCommand, Command removeAllByEnginePowerCommand,
                          Command countByFuelTypeCommand, Command filterLessThanFuelTypeCommand) {
        commandMap.put(HelpCommand.getName(), helpCommand);
        commandMap.put(InfoCommand.getName(), infoCommand);
        commandMap.put(ShowCommand.getName(), showCommand);
        commandMap.put(InsertCommand.getName(), insertCommand);
        commandMap.put(UpdateCommand.getName(), updateCommand);
        commandMap.put(RemoveKeyCommand.getName(), removeKeyCommand);
        commandMap.put(ClearCommand.getName(), clearCommand);
        commandMap.put(SaveCommand.getName(), saveCommand);
        commandMap.put(ExecuteScriptCommand.getName(), executeScriptCommand);
        commandMap.put(ExitCommand.getName(), exitCommand);
        commandMap.put(RemoveGreaterCommand.getName(), removeGreaterCommand);
        commandMap.put(RemoveLowerCommand.getName(), removeLowerCommand);
        commandMap.put(RemoveGreaterKeyCommand.getName(), removeGreaterKeyCommand);
        commandMap.put(RemoveAllByEnginePowerCommand.getName(), removeAllByEnginePowerCommand);
        commandMap.put(CountByFuelTypeCommand.getName(), countByFuelTypeCommand);
        commandMap.put(FilterLessThanFuelTypeCommand.getName(), filterLessThanFuelTypeCommand);

        setReferenceFile();
    }

    /**
     * Fills the reference file.
     */
    private void setReferenceFile() {
        StringBuilder reference = new StringBuilder();
        commandMap.forEach((commandName, command) -> reference.append(command).append("\n"));
        FileHandler.writeReferenceFile(reference.toString());
    }

    /**
     * Invokes the 'help' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean help(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }

    /**
     * Invokes the 'info' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean info(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }

    /**
     * Invokes the 'show' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean show(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }

    /**
     * Invokes the 'insert' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean insert(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }

    /**
     * Invokes the 'update' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean update(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }

    /**
     * Invokes the 'remove key' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeKey(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }

    /**
     * Invokes the 'clear' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean clear(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }

    /**
     * Invokes the 'save' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean save(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }

    /**
     * Invokes the 'execute script' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean executeScript(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }

    /**
     * Invokes the 'help' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean exit(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }

    /**
     * Invokes the 'remove greater' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeGreater(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }

    /**
     * Invokes the 'remove lower' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeLower(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }

    /**
     * Invokes the 'remove greater key' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeGreaterKey(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }

    /**
     * Invokes the 'remove all by engine power' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeAllByEnginePower(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }

    /**
     * Invokes the 'count by fuel type' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean countByFuelType(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }

    /**
     * Invokes the 'filter less than fuel type' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean filterLessThanFuelType(CommandArguments commandArguments) {
        return commandMap.get(commandArguments.commandName()).execute(commandArguments);
    }
}
