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
    private final Queue<CommandArguments> commandArgumentsQueue = new LinkedList<>();

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
        Scanner scanner = new Scanner(System.in);
        Console.println("Available commands:");
        Console.println(FileHandler.readFile(FileType.REFERENCE));
        ServerAnswer serverAnswer = null;
        do {
            try {
                if (commandArgumentsQueue.isEmpty()) { // если обработали все команды, то вводим новые
                    CommandArgumentsBuilder commandArgumentsBuilder = new CommandArgumentsBuilder(scanner);
                    commandArgumentsQueue.addAll(commandArgumentsBuilder.userEnter());
                }
                commandArguments = commandArgumentsQueue.remove();
                CommandValidator commandValidator = new CommandValidator(AnswerType.EXECUTION_RESPONSE);
                if (!commandValidator.validate(commandArguments)) {// if arguments or command was wrong, request data again
                    Console.printUserErrors();
                    MessageHolder.clearMessages(MessageType.USER_ERROR);
                    commandArguments = null;
                    continue;
                }
                //need connection check
//                if (!client.isServerAlive()) {
//                    //тестовый обмен данными
//                    System.out.println("connection lost");
//                    break;
//                }
//                if (commandArguments == null) //if user just press Enter bottom
//                    continue;
//                System.out.println(commandArguments + "");
                if (commandArguments.getCommandName().equals(ExitCommand.getName())) {
                    System.out.println("client exit");
                    break;
                }
                serverAnswer = client.dataExchange(commandArguments);
                if (serverAnswer == null) {
                    teardown();
                    System.out.println("соединение с сервером потеряно");
                    System.out.println("Is connected to server: "+ client.getSocketChannel().isConnected());
                    return false;
                }

//                System.out.println(serverAnswer + "");
                if (serverAnswer.answerType() == AnswerType.DATA_REQUEST) {
                    //insert mode (new fields for Vehicle), change commandArguments
                    if (commandArguments.getExecuteMode() == ExecuteMode.COMMAND_MODE) {
                        Vehicle vehicle = Console.insertMode(0, null);
                    }

                    System.out.println("_____INSERT_MODE______");
                    serverAnswer = client.dataExchange(commandArguments);
                    if (serverAnswer == null) {
                        teardown();
                        System.out.println("соединение прервано, команда не была выполнена");
                        return false;
                    }
                }
                //print command result
                if (serverAnswer.commandExitStatus()) {
                    serverAnswer.outputInfo().forEach(System.out::println);
                } else {
                    serverAnswer.userErrors().forEach(System.out::println);
                }

            } catch (NoSuchElementException e) {
                teardown();
                return false;
            }
        } while (commandArguments == null || !commandArguments.getCommandName().equals(ExitCommand.getName()));
        teardown();
        return true;
    }

    private void teardown() {
        Client.stop();
        scanner.close();
    }

}
