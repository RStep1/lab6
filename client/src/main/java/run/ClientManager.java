package run;

import commands.ExitCommand;
import mods.AnswerType;
import mods.ExecuteMode;
import utility.CommandArguments;
import utility.ServerAnswer;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientManager {
    Client client;
    ServerAnswer serverAnswer;
    Scanner scanner;
    CommandArguments commandArguments;

    public ClientManager(Scanner scanner) {
        this.scanner = scanner;
    }

    public void setConnection(String host, int port) {
        this.client = Client.start(host, port);
    }

    public boolean interactiveMode() {
        if (client == null) {
            System.out.println("client = null");
            return false;
        }
        Scanner scanner = new Scanner(System.in);
        do {
            try {
                String nextLine = scanner.nextLine().trim();
                //command processing
                //validate command and arguments, build commandArguments
                commandArguments = new CommandArguments(nextLine, null, null, ExecuteMode.COMMAND_MODE);//example

                serverAnswer = client.sendRequest(commandArguments);
                if (serverAnswer == null) {
                    teardown();
                    System.out.println("response = null");
                    return false;
                }
                if (serverAnswer.getAnswerType() == AnswerType.DATA_REQUEST) {
                    //insert mode (new fields for Vehicle), change commandArguments
                    serverAnswer = client.sendRequest(commandArguments);
                    if (serverAnswer == null) {
                        teardown();
                        System.out.println("соединение прервано, команда не была выполнена");
                        return false;
                    }
                } else {
                    //print command result
                }
            } catch (NoSuchElementException e) {
                teardown();
                e.printStackTrace();
            }
        } while (commandArguments.commandName().equals(ExitCommand.getName()));
        teardown();
        return true;
    }

    private boolean checkServerAnswer(ServerAnswer serverAnswer) {
        if (serverAnswer == null) {
            teardown();
        }
    }

    private void teardown() {
        Client.stop();
        scanner.close();
    }
}
