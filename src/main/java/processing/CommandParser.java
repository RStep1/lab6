package processing;

import commands.ExitCommand;
import commands.InsertCommand;
import commands.UpdateCommand;
import data.Vehicle;
import mods.ExecuteMode;
import mods.FileType;
import utility.FileHandler;

import java.util.ArrayList;

/**
 * Processes the lines entered by the user.
 * Divides them into commands and arguments and calls them.
 */
public class CommandParser {
    private CommandInvoker invoker;
    private ArrayList<String> scriptLines;

    /**
     * Used in command mode.
     */
    public CommandParser(CommandInvoker invoker) {
        this.invoker = invoker;
    }

    /**
     * Used in script mode.
     * @param scriptLines Lines from a user-run script.
     */
    public CommandParser(CommandInvoker invoker, ArrayList<String> scriptLines) {
        this.invoker = invoker;
        this.scriptLines = scriptLines;
    }

    /**
     * Defines and runs a specific command.
     * @param nextLine Current processed line.
     * @param nextCommand Current processed command.
     * @return Command exit status.
     */
    private boolean commandSelection(String nextLine, String nextCommand, String[] arguments,
                                     String[] vehicleValues, ExecuteMode executeMode) {
        boolean exitStatus;
        switch (nextCommand) {
            case "help" -> exitStatus = invoker.help(arguments, vehicleValues, executeMode);
            case "info" -> exitStatus = invoker.info(arguments, vehicleValues, executeMode);
            case "show" -> exitStatus = invoker.show(arguments, vehicleValues, executeMode);
            case "insert" -> exitStatus = invoker.insert(arguments, vehicleValues, executeMode);
            case "update" -> exitStatus = invoker.update(arguments, vehicleValues, executeMode);
            case "remove_key" -> exitStatus = invoker.removeKey(arguments, vehicleValues, executeMode);
            case "clear" -> exitStatus = invoker.clear(arguments, vehicleValues, executeMode);
            case "save" -> exitStatus = invoker.save(arguments, vehicleValues, executeMode);
            case "execute_script" -> exitStatus = invoker.executeScript(arguments, vehicleValues, executeMode);
            case "exit" -> exitStatus = invoker.exit(arguments, vehicleValues, executeMode);
            case "remove_greater" -> exitStatus = invoker.removeGreater(arguments, vehicleValues, executeMode);
            case "remove_lower" -> exitStatus = invoker.removeLower(arguments, vehicleValues, executeMode);
            case "remove_greater_key" -> exitStatus = invoker.removeGreaterKey(arguments, vehicleValues, executeMode);
            case "remove_all_by_engine_power" -> exitStatus =
                    invoker.removeAllByEnginePower(arguments, vehicleValues, executeMode);
            case "count_by_fuel_type" -> exitStatus = invoker.countByFuelType(arguments, vehicleValues, executeMode);
            case "filter_less_than_fuel_type" -> exitStatus =
                    invoker.filterLessThanFuelType(arguments, vehicleValues, executeMode);
            default -> {
                FileHandler.writeToFile(String.format("'%s': No such command", nextLine.trim()), FileType.USER_ERRORS);
                exitStatus = false;
            }
        }
        return exitStatus;
    }

    /**
     * Separates a command from its arguments.
     */
    private static class UserLineSeparator {
        private final String command;
        private final String[] arguments;
        public UserLineSeparator(String nextLine) {
            String[] nextSplitedLine = nextLine.trim().split("\\s+");
            this.arguments = new String[nextSplitedLine.length - 1];
            for (int i = 1; i < nextSplitedLine.length; i++) {
                this.arguments[i - 1] = nextSplitedLine[i];
            }
            this.command = nextSplitedLine[0];
        }
        public String getCommand() {
            return command;
        }
        public String[] getArguments() {
            return arguments;
        }
    }

    /**
     * Processes user input strings and starts command fetching.
     * @param nextLine Another line entered by the user.
     * @return Command processing exit status.
     */
    public boolean commandProcessing(String nextLine) {
        if (nextLine.trim().equals(""))
            return true;
        UserLineSeparator userLineSeparator = new UserLineSeparator(nextLine);
        String nextCommand = userLineSeparator.getCommand();
        String[] arguments = userLineSeparator.getArguments();
        String[] extraArguments = new String[0];
        boolean exitStatus = commandSelection(nextLine, nextCommand, arguments,
                extraArguments, ExecuteMode.COMMAND_MODE);
        Console.printOutputFile();
        if (!FileHandler.readFile(FileType.USER_ERRORS).isEmpty()) {
            FileHandler.writeToFile(Console.getHelpMessage(), FileType.USER_ERRORS);
            Console.printUserErrorsFile();
        }
        FileHandler.clearFile(FileType.OUTPUT);
        FileHandler.clearFile(FileType.USER_ERRORS);
        if (nextCommand.equals(ExitCommand.getName()) && exitStatus)
            return false;
        return true;
    }

    /**
     * Processes each line of user script.
     * @return Script execution status.
     */
    public boolean scriptProcessing(String scriptName) {
        int countOfLines = scriptLines.size();
        boolean hasCommands = false;
        for (int lineIndex = 0; lineIndex < countOfLines; lineIndex++) {
            String nextLine = scriptLines.get(lineIndex);
            if (nextLine.trim().equals(""))
                continue;
            hasCommands = true;
            UserLineSeparator userLineSeparator = new UserLineSeparator(nextLine);
            String nextCommand = userLineSeparator.getCommand();
            String[] arguments = userLineSeparator.getArguments();
            int countOfExtraArguments = 0;
            if (nextCommand.equals(InsertCommand.getName()) || nextCommand.equals(UpdateCommand.getName()))
                countOfExtraArguments = Vehicle.getCountOfChangeableFields();
            String[] extraArguments = setExtraArguments(countOfExtraArguments, lineIndex, countOfLines);
            boolean exitStatus = commandSelection(nextLine, nextCommand, arguments,
                    extraArguments, ExecuteMode.SCRIPT_MODE);
            lineIndex += extraArguments.length;
            if (exitStatus && nextCommand.equals(ExitCommand.getName())) {
                FileHandler.writeCurrentCommand(ExitCommand.getName(), FileType.OUTPUT);
                FileHandler.writeToFile(String.format(
                        "Script '%s' successfully completed", scriptName), FileType.OUTPUT);
                return true;
            }
        }
        if (!hasCommands)
            FileHandler.writeToFile(String.format("Script '%s' is empty", scriptName), FileType.USER_ERRORS);
        return true;
    }

    /**
     * Adds extra lines from script that are used as arguments to change the collection element.
     */
    private String[] setExtraArguments(int countOfExtraArguments, int currentLineIndex, int countOfScriptLines) {
        String[] extraArguments =
                new String[Math.min(countOfExtraArguments, countOfScriptLines - currentLineIndex - 1)];
        for (int i = 0, j = currentLineIndex + 1;
             j < currentLineIndex + countOfExtraArguments + 1 && j < countOfScriptLines; j++, i++)
            extraArguments[i] = scriptLines.get(j).trim();
        return extraArguments;
    }
}
