package run;

import mods.FileType;
import processing.*;
import commands.*;
import utility.FileHandler;

import java.util.ArrayList;
import java.util.Optional;

/**
 * The entry point to the program, declares and initializes all the necessary classes.
 * Starts interactive mode for the user.
 */
public class Main {
    public static void main(String[] args) {
        if (!FileHandler.checkEnvVariable()) {
            Console.printUserErrorsFile();
            FileHandler.clearFile(FileType.USER_ERRORS);
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
