package user;

import commands.ExitCommand;
import mods.*;
import processing.CommandArgumentsBuilder;
import processing.Console;
import utility.CommandArguments;
import utility.MessageHolder;
import utility.ServerAnswer;
import utility.FileHandler;

import java.io.IOException;
import java.util.*;

public class ClientManager {
    private Client client;
    private final Scanner scanner;
    private CommandArguments commandArguments;
    private final Queue<CommandArguments> commandArgumentsQueue = new LinkedList<>();

    public ClientManager(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean setConnection(String host, int port) {
        try {
            this.client = new Client(host, port);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean processRequestToServer() {
        Console.println("Available commands:");
        Console.println(FileHandler.readFile(FileType.REFERENCE));
        ServerAnswer serverAnswer = null;
        do {
            try {
                if (commandArgumentsQueue.isEmpty()) { //if all commands have been processed, then we enter new ones
                    CommandArgumentsBuilder commandArgumentsBuilder = new CommandArgumentsBuilder(scanner, AnswerType.EXECUTION_RESPONSE);
                    commandArgumentsQueue.addAll(commandArgumentsBuilder.userEnter());
                    Console.printUserErrors();
                    MessageHolder.clearMessages(MessageType.USER_ERROR);
                }
                if (commandArgumentsQueue.isEmpty()) {// if input is empty or it has mistakes
                    commandArguments = null;
                    continue;
                }
                commandArguments = commandArgumentsQueue.peek();
                if (commandArguments.getCommandName().equals(ExitCommand.getName())) {
                    if (commandArguments.getExecuteMode() == ExecuteMode.SCRIPT_MODE) {
                        System.out.println("Command exit:");
                        System.out.println(String.format("Script '%s' successfully completed",
                                commandArguments.getScriptFile().getName()));
                        commandArguments = null;
                        commandArgumentsQueue.remove();
                        continue;
                    }
                    System.out.println("client exit");
                    break;
                }
                serverAnswer = client.dataExchange(commandArguments);
                if (serverAnswer == null) {
                    Client.stop();
                    Console.println("Connection lost");
                    return false;
                }
                if (serverAnswer.answerType() == AnswerType.DATA_REQUEST && serverAnswer.commandExitStatus()) {
                    if (commandArguments.getExecuteMode() == ExecuteMode.COMMAND_MODE) {
                        String[] extraArguments = Console.insertMode();
                        commandArguments.setExtraArguments(extraArguments);
                    }
                    serverAnswer = client.dataExchange(commandArguments);
                    if (serverAnswer == null) {
                        Client.stop();
                        Console.println("Connection lost");
                        return false;
                    }
                }
                commandArgumentsQueue.remove();
                Console.printOutputInfo(serverAnswer.outputInfo());
                Console.printUserErrors(serverAnswer.userErrors());
            } catch (NoSuchElementException e) {
                Client.stop();
                return false;
            }
        } while (commandArguments == null || !commandArguments.getCommandName().equals(ExitCommand.getName()));
        
        Client.stop();
        return true;
    }
}
