package processing;

import commands.ExitCommand;
import mods.ExecuteMode;
import mods.MessageType;
import run.Client;
import utility.CommandArguments;
import utility.MessageHolder;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class CommandArgumentsBuilder {
    public final Scanner scanner;

    public CommandArgumentsBuilder(Scanner scanner) {
        this.scanner = scanner;
    }
    public CommandArguments userEnter() {
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

    private CommandArguments commandProcessing(String nextLine) {
        if (nextLine.trim().equals(""))
            return null;
        UserLineSeparator userLineSeparator = new UserLineSeparator(nextLine);
        String nextCommand = userLineSeparator.getCommand();
        String[] arguments = userLineSeparator.getArguments();
        String[] extraArguments = new String[0];
        return new CommandArguments(nextCommand, arguments, extraArguments, ExecuteMode.COMMAND_MODE);
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
}
