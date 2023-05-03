package processing;

import commands.ExecuteScriptCommand;
import commands.ExitCommand;
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

    public CommandArgumentsBuilder(Scanner scanner) {
        this.scanner = scanner;
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
        return commandProcessing(nextLine);
    }

    private ArrayList<CommandArguments> commandProcessing(String nextLine) {
        if (nextLine.trim().equals(""))
            return null;
        UserLineSeparator userLineSeparator = new UserLineSeparator(nextLine);
        String nextCommand = userLineSeparator.getCommand();
        String[] arguments = userLineSeparator.getArguments();
        String[] extraArguments = null;
        CommandArguments newCommandArguments = new CommandArguments(nextCommand, arguments, extraArguments, ClientRequestType.COMMAND_EXECUTION, ExecuteMode.COMMAND_MODE);
        if (nextCommand.equals(ExecuteScriptCommand.getName()))
            return scriptProcessing(newCommandArguments);
        ArrayList<CommandArguments> commandArgumentsArrayList = new ArrayList<>();
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
        File scriptFile = FileHandler.findFile(new File("scripts"), commandArguments.getArguments()[0]);
        ArrayList<String> scriptLines = FileHandler.readScriptFile(scriptFile);
        ArrayList<CommandArguments> scriptCommands = new ArrayList<>();
        scriptLines.forEach(scriptLine -> scriptCommands.addAll(commandProcessing(scriptLine)));
        return scriptCommands;
    }
}
