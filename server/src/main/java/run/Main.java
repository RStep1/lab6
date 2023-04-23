package run;

import mods.MessageType;
import processing.BufferedDataBase;
import processing.Console;
import processing.CommandInvoker;
import commands.*;
import utility.FileHandler;
import utility.MessageHolder;


/**
 * The entry point to the program, declares and initializes all the necessary classes.
 * Starts interactive mode for the user.
 */
public class Main {
    public static void main(String[] args) {
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
        Console console = new Console(invoker);
        bufferedDataBase.setCommandInvoker(invoker);
        console.interactiveMode();
    }
}
