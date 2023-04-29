package processing;

import commands.*;
import exceptions.WrongAmountOfArgumentsException;
import mods.AnswerType;
import mods.MessageType;
import utility.CommandArguments;
import utility.MessageHolder;
import utility.ServerAnswer;

import java.util.Map;

public class CommandValidator {
    private AnswerType answerType;

    public CommandValidator(AnswerType answerType) {
        this.answerType = answerType;
    }

    public static boolean checkNumberOfArguments(String[] arguments, int expectedNumberOfArguments,
                                                 String commandName) {
        try {
            if (arguments.length != expectedNumberOfArguments) {
                MessageHolder.putCurrentCommand(commandName, MessageType.USER_ERROR);
                throw new WrongAmountOfArgumentsException("Wrong amount of arguments: ",
                        arguments.length, expectedNumberOfArguments);
            }
            return true;
        } catch (WrongAmountOfArgumentsException e) {
            MessageHolder.putMessage(e.getMessage(), MessageType.USER_ERROR);
        }
        return false;
    }

    /**
//     * Checks the arguments of commands that use the key as an argument.
//     * @param arguments Arguments which entered on the same line as the command.
//     * @return Check status.
//     */
//    private boolean checkCommandWithKey(String[] arguments, String commandName) {
//        if (arguments.length == 0) {
//            MessageHolder.putCurrentCommand(commandName, MessageType.USER_ERROR);
//            MessageHolder.putMessage("Key value cannot be null", MessageType.USER_ERROR);
//            return false;
//        }
//        if (!checkNumberOfArguments(arguments, 1, commandName + " " + arguments[0]))
//            return false;
//        if (!identifierHandler.checkKey(arguments[0], commandName + " " + arguments[0]))
//            return false;
//        return true;
//    }

    public boolean validate(CommandArguments commandArguments) {
        int correctCountOfArguments = 0;
        Command currentCommand;
        switch (commandArguments.commandName()) {
            case "help" -> correctCountOfArguments = HelpCommand.getCountOfArguments();
            case "info" -> correctCountOfArguments = InfoCommand.getCountOfArguments();
            case "show" -> correctCountOfArguments = ShowCommand.getCountOfArguments();
            case "insert" -> correctCountOfArguments = InsertCommand.getCountOfArguments();
            case "update" -> correctCountOfArguments = UpdateCommand.getCountOfArguments();
            case "remove_key" -> correctCountOfArguments = RemoveKeyCommand.getCountOfArguments();
            case "clear" -> correctCountOfArguments = ClearCommand.getCountOfArguments();
            case "save" -> correctCountOfArguments = SaveCommand.getCountOfArguments();
            case "execute_script" -> correctCountOfArguments = ExecuteScriptCommand.getCountOfArguments();
            case "exit" -> correctCountOfArguments = ExitCommand.getCountOfArguments();
            case "remove_greater" -> correctCountOfArguments = RemoveGreaterCommand.getCountOfArguments();
            case "remove_lower" -> correctCountOfArguments = RemoveLowerCommand.getCountOfArguments();
            case "remove_greater_key" -> correctCountOfArguments = RemoveGreaterKeyCommand.getCountOfArguments();
            case "remove_all_by_engine_power" -> correctCountOfArguments = RemoveAllByEnginePowerCommand.getCountOfArguments();
            case "count_by_fuel_type" -> correctCountOfArguments = CountByFuelTypeCommand.getCountOfArguments();
            case "filter_less_than_fuel_type" -> correctCountOfArguments = FilterLessThanFuelTypeCommand.getCountOfArguments();
            default -> {
                MessageHolder.putMessage(String.format(
                        "'%s': No such command", commandArguments.commandName()), MessageType.USER_ERROR);
                return false;
            }
        }
        if (!checkNumberOfArguments(commandArguments.arguments(),
                correctCountOfArguments, commandArguments.commandName()))
            return false;
        if (correctCountOfArguments == 0)
            return true;

//        Map<String, Command> commandMap = CommandInvoker.getCommandMap();
        return true;
    }

}
