package run;

import commands.ExecuteScriptCommand;
import commands.ExitCommand;
import data.Vehicle;
import mods.*;
import processing.CommandArgumentsBuilder;
import processing.CommandValidator;
import processing.Console;
import utility.CommandArguments;
import utility.FileHandler;
import utility.MessageHolder;
import utility.ServerAnswer;

import java.io.IOException;
import java.util.*;

public class ClientManager {
    private Client client;
    private final Scanner scanner;
    private CommandArguments commandArguments;
    // private final Queue<CommandArguments> commandArgumentsQueue = new LinkedList<>();

    public ClientManager(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean setConnection(String host, int port) {
        try {
            this.client = new Client(host, port);
        } catch (IOException e) {
//            System.out.println("SERVER NOT ANSWER");
            return false;
        }
        return true;
    }

    public boolean processRequestToServer() {
        Console.println("Available commands:");
        // Console.println(FileHandler.readFile(FileType.REFERENCE));
        Queue<CommandArguments> commandArgumentsQueue = new LinkedList<>();
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
                // System.out.println(commandArgumentsQueue);
                // commandArguments = commandArgumentsQueue.remove();
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
                // System.out.println(commandArguments + "");
                serverAnswer = client.dataExchange(commandArguments);
                if (serverAnswer == null) {
                    Client.stop();
                    System.out.println("соединение ссервером потеряно");
                    System.out.println("Is connected to server: "+ client.getSocketChannel().isConnected());
                    return false;
                }
                commandArgumentsQueue.remove();
                if (serverAnswer.answerType() == AnswerType.DATA_REQUEST && serverAnswer.commandExitStatus()) {
                    //insert mode (new fields for Vehicle), change commandArguments
                    if (commandArguments.getExecuteMode() == ExecuteMode.COMMAND_MODE) {
                        String[] extraArguments = Console.insertMode();
                        commandArguments.setExtraArguments(extraArguments);
                    }
                    // System.out.println("_____INSERT_MODE______");
                    // System.out.println(serverAnswer);
                    serverAnswer = client.dataExchange(commandArguments);
                    // System.out.println(serverAnswer);
                    if (serverAnswer == null) {
                        Client.stop();
                        System.out.println("соединение прервано, команда не была выполнена");
                        return false;
                    }
                }
                // serverAnswer.outputInfo().forEach(System.out::println);
                // serverAnswer.userErrors().forEach(System.out::println);
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
