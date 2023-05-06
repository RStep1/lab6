package processing;

import commands.ExecuteScriptCommand;
import commands.ExitCommand;
import commands.InsertCommand;
import commands.UpdateCommand;
import data.Vehicle;
import mods.AnswerType;
import mods.ClientRequestType;
import mods.ExecuteMode;
import run.Client;
import utility.CommandArguments;
import utility.FileHandler;

import java.io.File;
import java.util.*;

public class CommandArgumentsBuilder {
    public final Scanner scanner;
    private final AnswerType answerType;

    public CommandArgumentsBuilder(Scanner scanner, AnswerType answerType) {
        this.scanner = scanner;
        this.answerType = answerType;
    }

    public ArrayList<CommandArguments> userEnter() {
        Console.print("Type command and press Enter: ");
        String nextLine = "";
        try {
            nextLine = scanner.nextLine();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            Client.stop();
            scanner.close();
            System.exit(0);
        }
        return commandProcessing(nextLine, ExecuteMode.COMMAND_MODE, null);
    }

    private ArrayList<CommandArguments> commandProcessing(String nextLine, ExecuteMode executeMode, File currentScriptFile) {
        if (nextLine.trim().equals(""))
            return new ArrayList<>();
        UserLineSeparator userLineSeparator = new UserLineSeparator(nextLine);
        String nextCommand = userLineSeparator.getCommand();
        String[] arguments = userLineSeparator.getArguments();
        String[] extraArguments = null;
        // System.out.println(nextLine);
        CommandArguments newCommandArguments = new CommandArguments(nextCommand, arguments, extraArguments,
                ClientRequestType.COMMAND_EXECUTION, executeMode);
        newCommandArguments.setScriptFile(currentScriptFile);
        if (nextCommand.equals(ExecuteScriptCommand.getName())) // if it's execute_script command, start script processing
            return scriptProcessing(newCommandArguments);
        ArrayList<CommandArguments> commandArgumentsArrayList = new ArrayList<>();
        CommandValidator commandValidator = new CommandValidator(answerType);
        if (commandValidator.validate(newCommandArguments)) {// add command only if it's correct
            // System.out.println("COMMAND: " + newCommandArguments.getCommandName() + " " + Arrays.asList(newCommandArguments.getArguments()));
            commandArgumentsArrayList.add(newCommandArguments);
        }
        return commandArgumentsArrayList;
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

    private ArrayList<CommandArguments> scriptProcessing(CommandArguments commandArguments) {
        CommandValidator commandValidator = new CommandValidator(answerType);
        if (!commandValidator.validate(commandArguments))
            return new ArrayList<>();
        File currentScriptFile = commandArguments.getScriptFile();
        ArrayList<String> scriptLines = FileHandler.readScriptFile(commandArguments.getScriptFile());
        ArrayList<CommandArguments> scriptCommands = new ArrayList<>();
        int countOfScriptLines = scriptLines.size();
        for (int line = 0; line < countOfScriptLines; line++) {
            String scriptLine = scriptLines.get(line);
            if (scriptLine.trim().equals(""))
                continue;
            scriptCommands.addAll(commandProcessing(scriptLine, ExecuteMode.SCRIPT_MODE, currentScriptFile));
            if (!scriptCommands.isEmpty()) {
                CommandArguments lastCommandArguments = scriptCommands.get(scriptCommands.size() - 1);
                if (lastCommandArguments.getCommandName().equals(ExitCommand.getName()) &&
                    lastCommandArguments.getScriptFile().getName().equals(currentScriptFile.getName())) { // exit from script, stop adding commands (only if the last command from current scrip file)
                    break;
                }
                if ((lastCommandArguments.getCommandName().equals(InsertCommand.getName()) || 
                    lastCommandArguments.getCommandName().equals(UpdateCommand.getName())) && 
                    lastCommandArguments.getScriptFile().getName().equals(currentScriptFile.getName()) &&
                    lastCommandArguments.getExtraArguments() == null) {
                    String[] extraArguments = readExtraArguments(Vehicle.getCountOfChangeableFields(),
                                                                     line, countOfScriptLines, scriptLines);
                    lastCommandArguments.setExtraArguments(extraArguments);
                    // System.out.println(lastCommandArguments);
                    line += Vehicle.getCountOfChangeableFields();
                }
            }
        }
        return scriptCommands;
    }

     /**
     * Adds extra lines from script that are used as arguments to change the collection element.
     */
    private String[] readExtraArguments(int countOfExtraArguments, int currentLineIndex,
                                        int countOfScriptLines, ArrayList<String> scriptLines) {
        String[] extraArguments =
                new String[Math.min(countOfExtraArguments, countOfScriptLines - currentLineIndex - 1)];
        for (int i = 0, j = currentLineIndex + 1; 
        j < currentLineIndex + countOfExtraArguments + 1 && j < countOfScriptLines; j++, i++)
            extraArguments[i] = scriptLines.get(j).trim();
        return extraArguments;
    }
}
