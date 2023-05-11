package run;

import mods.FileType;
import mods.MessageType;
import processing.BufferedDataBase;
import processing.Console;
import processing.RequestHandler;
import processing.CommandInvoker;

import java.util.Scanner;

import commands.*;
import host.Server;
import utility.FileHandler;
import utility.MessageHolder;
import utility.ScriptGenerator;


/**
 * The entry point to the program, declares and initializes all the necessary classes.
 * Starts interactive mode for the user.
 */
public class Main {
    public static void main(String[] args) {
        // FileHandler.clearFile(FileType.TEST_SCRIPT);
        // ScriptGenerator scriptGenerator = new ScriptGenerator(50000);
        // scriptGenerator.generateInserts();


        if (!FileHandler.checkEnvVariable()) {
            Console.printUserErrors();
            MessageHolder.clearMessages(MessageType.USER_ERROR);
            return;
        }
        BufferedDataBase bufferedDataBase = new BufferedDataBase();
        CommandInvoker invoker = new CommandInvoker(new HelpCommand(bufferedDataBase),
                new InfoCommand(bufferedDataBase), new ShowCommand(bufferedDataBase),
                new InsertCommand(bufferedDataBase), new UpdateCommand(bufferedDataBase),
                new RemoveKeyCommand(bufferedDataBase), new ClearCommand(bufferedDataBase),
                new SaveCommand(bufferedDataBase), new ExecuteScriptCommand(bufferedDataBase),
                new ExitCommand(bufferedDataBase), new RemoveGreaterCommand(bufferedDataBase),
                new RemoveLowerCommand(bufferedDataBase),
                new RemoveGreaterKeyCommand(bufferedDataBase),
                new RemoveAllByEnginePowerCommand(bufferedDataBase),
                new CountByFuelTypeCommand(bufferedDataBase),
                new FilterLessThanFuelTypeCommand(bufferedDataBase));
        RequestHandler requestHandler = new RequestHandler(invoker);
        Server server = new Server(requestHandler);
        bufferedDataBase.setCommandInvoker(invoker);
        Console.println("Server is running...");
        
        
        Thread mainProggrammThread = new Thread(new Runnable() {
            public void run() {
                server.run();
            }
        });
        mainProggrammThread.start();


        Scanner scanner = new Scanner(System.in);
         while (true) {
            String nextLine = scanner.nextLine().trim();
            if (nextLine.equals(SaveCommand.getName())) {
                requestHandler.processRequest(Server.getSaveCommand());
                Console.println("Collection was successfully saved");
            }
            if (!mainProggrammThread.isAlive()) {
                break;
            }
        }
        scanner.close();
    }
}
