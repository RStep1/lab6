package run;

import commands.ExitCommand;
import mods.AnswerType;
import mods.ExecuteMode;
import mods.FileType;
import mods.MessageType;
import processing.CommandArgumentsBuilder;
import processing.CommandParser;
import processing.CommandValidator;
import processing.Console;
import utility.CommandArguments;
import utility.FileHandler;
import utility.ServerAnswer;


import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientManager {
    private Client client;
    private final Scanner scanner;
    private CommandArguments commandArguments;

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
        do {
            try {
                CommandArgumentsBuilder commandArgumentsBuilder = new CommandArgumentsBuilder(scanner);
                commandArguments = commandArgumentsBuilder.userEnter();
                //need connection check
//                if (!client.isServerAlive()) {
//                    //тестовый обмен данными
//                    System.out.println("connection lost");
//                    break;
//                }
                if (commandArguments == null) //if user just press Enter bottom
                    continue;
                System.out.println(commandArguments + "");
                CommandValidator commandValidator = new CommandValidator(AnswerType.EXECUTION_RESPONSE);
                if (!commandValidator.validate(commandArguments)) // if arguments or command was wrong, request data again
                    continue;

                // (1) command processing
                // (2) build commandArguments object
                // (3) validate command and arguments
                // (4) serialization
                // (5) send obj to server
//                commandArguments = new CommandArguments("nextLine", null, null, ExecuteMode.COMMAND_MODE);//example
                if (commandArguments.commandName().equals(ExitCommand.getName())) {
                    System.out.println("client exit");
                    break;
                }
                ServerAnswer serverAnswer = client.sendRequest(commandArguments);
                if (serverAnswer == null) {
                    teardown();
                    System.out.println("соединение с сервером потеряно");
                    System.out.println("Is connected to server: "+ client.getSocketChannel().isConnected());
                    return false;
                }

                System.out.println(serverAnswer);
                if (serverAnswer.getAnswerType() == AnswerType.DATA_REQUEST) {
                    //insert mode (new fields for Vehicle), change commandArguments
                    serverAnswer = client.sendRequest(commandArguments);
                    if (serverAnswer == null) {
                        teardown();
                        System.out.println("соединение прервано, команда не была выполнена");
                        return false;
                    }
                }
                //print command result
                System.out.println(serverAnswer.getOutputInfo() + "   answer");//

            } catch (NoSuchElementException e) {
                teardown();
                return false;
            }
        } while (commandArguments == null || !commandArguments.commandName().equals(ExitCommand.getName()));
        teardown();
        return true;
    }

    private void teardown() {
        Client.stop();
        scanner.close();
    }

}
