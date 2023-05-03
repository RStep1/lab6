package processing;

import commands.ExecuteScriptCommand;
import commands.ExitCommand;
import mods.AnswerType;
import mods.ClientRequestType;
import mods.ExecuteMode;
import mods.MessageType;
import run.Client;
import utility.CommandArguments;
import utility.FileHandler;
import utility.MessageHolder;

import java.io.File;
import java.util.*;

public class CommandArgumentsBuilder {
    public final Scanner scanner;
    public final AnswerType answerType;

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
            Client.stop();
            scanner.close();
            System.exit(0);
        }
        return commandProcessing(nextLine, ExecuteMode.COMMAND_MODE);
    }

    private ArrayList<CommandArguments> commandProcessing(String nextLine, ExecuteMode executeMode) {
        if (nextLine.trim().equals(""))
            return new ArrayList<>();
        UserLineSeparator userLineSeparator = new UserLineSeparator(nextLine);
        String nextCommand = userLineSeparator.getCommand();
        String[] arguments = userLineSeparator.getArguments();
        String[] extraArguments = null;
        CommandArguments newCommandArguments = new CommandArguments(nextCommand, arguments, extraArguments,
                ClientRequestType.COMMAND_EXECUTION, executeMode);
        if (nextCommand.equals(ExecuteScriptCommand.getName())) // if it's a execute_script command
            return scriptProcessing(newCommandArguments);
        ArrayList<CommandArguments> commandArgumentsArrayList = new ArrayList<>();
        CommandValidator commandValidator = new CommandValidator(answerType);
        if (commandValidator.validate(newCommandArguments)) // add command only if it's correct
            commandArgumentsArrayList.add(newCommandArguments);
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
        File scriptFile = FileHandler.findFile(new File("scripts"), commandArguments.getArguments()[0]);
        ArrayList<String> scriptLines = FileHandler.readScriptFile(scriptFile);
        ArrayList<CommandArguments> scriptCommands = new ArrayList<>();
        scriptLines.forEach(scriptLine -> scriptCommands.addAll(commandProcessing(scriptLine, ExecuteMode.SCRIPT_MODE).stream().filter(commandValidator::validate).toList()));
//        scriptLines.forEach(scriptLine -> scriptCommands.addAll(commandProcessing(scriptLine, ExecuteMode.SCRIPT_MODE)));

        return scriptCommands;
    }
}
