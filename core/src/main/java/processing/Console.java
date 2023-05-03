package processing;

import data.Vehicle;
import mods.FileType;
import mods.MessageType;
import utility.Process;
import utility.*;


import java.io.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Designed for input-output information.
 */
public class Console {
    private final CommandInvoker invoker;
    private static final String helpMessage = "Type 'help' and press Enter to see a list of commands";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    public Console(CommandInvoker invoker) {
        this.invoker = invoker;
    }

    /**
     * Reads user lines one by one and starts processing.
     */
    public void interactiveMode() {
        MessageHolder.putMessage("Available commands:", MessageType.OUTPUT_INFO);
        MessageHolder.putMessage(FileHandler.readFile(FileType.REFERENCE), MessageType.OUTPUT_INFO);
        printOutputInfo();
        MessageHolder.clearMessages(MessageType.OUTPUT_INFO);
        MessageHolder.clearMessages(MessageType.USER_ERROR);
        CommandParser parser = new CommandParser(invoker);
        Scanner in = new Scanner(System.in);
        while (true) {
            PrintStream printStream = new PrintStream(System.out);
            printStream.print("Type command and press Enter: ");
            String nextLine = "";
            try {
                nextLine = in.nextLine();
            } catch (NoSuchElementException e) {
                System.exit(0);
            }
            boolean exitStatus = parser.commandProcessing(nextLine);
            if (!exitStatus)
                break;
        }
        in.close();
    }


    /**
     * Reads the parameters of the collection element.
     * @param id Automatically generated parameter.
     * @param creationDate Automatically generated parameter.
     * @return New formed collection element.
     */
    public static Vehicle insertMode(long id, java.time.ZonedDateTime creationDate) {
        Scanner in = new Scanner(System.in);
        PrintStream printStream = new PrintStream(System.out);
        String newName, newX, newY, newEnginePower, newDistanceTravelled, newType, newFuelType;
        ArrayList<String> newValues = new ArrayList<>();
        String newValue = "";
        ArrayList<Process> processes = ValueHandler.getValueProcesses();
        for (Process process : processes) {
            do {
                Console.printUserErrors();
                MessageHolder.clearMessages(MessageType.USER_ERROR);
                printStream.print(process.getMessage());
                try {
                    newValue = in.nextLine().trim();
                } catch (NoSuchElementException e) {
                    System.exit(0);
                }
                newValue = process.getCorrection().correct(newValue);
                CheckingResult checkingResult = process.getChecker().check(newValue);
                if (!checkingResult.getStatus())
                    MessageHolder.putMessage(checkingResult.getMessage(), MessageType.USER_ERROR);
            } while (!process.getChecker().check(newValue).getStatus());
            newValues.add(newValue);
        }
        newName = newValues.get(0);
        newX = newValues.get(1);
        newY = newValues.get(2);
        newEnginePower = newValues.get(3);
        newDistanceTravelled = newValues.get(4);
        newType = newValues.get(5);
        newFuelType = newValues.get(6);
        return ValueTransformer.createVehicle(id, newName, newX, newY, creationDate,
                newEnginePower, newDistanceTravelled, newType, newFuelType);
    }

    public static String getHelpMessage() {
        return helpMessage;
    }

    public static void print(String message) {
        PrintStream printStream = new PrintStream(System.out);
        printStream.print(message);
    }

    public static void println(String massage) {
        PrintStream printStream = new PrintStream(System.out);
        printStream.println(massage);
    }

    public static void printOutputInfo() {
        print(ANSI_GREEN + MessageHolder.getMessages(MessageType.OUTPUT_INFO) + ANSI_RESET);
    }

    public static void printUserErrors() {
        print(ANSI_RED + MessageHolder.getMessages(MessageType.USER_ERROR) + ANSI_RESET);
    }
}
