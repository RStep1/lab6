package processing;

import commands.*;
import exceptions.WrongAmountOfArgumentsException;
import mods.AnswerType;
import mods.ExecuteMode;
import mods.MessageType;
import utility.*;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class CommandValidator {
    private final AnswerType answerType;
    private static final Set<String> scriptCounter = new HashSet<>();

    public CommandValidator(AnswerType answerType) {
        this.answerType = answerType;
    }

    private boolean checkNumberOfArguments(CommandArguments commandArguments, int expectedNumberOfArguments) {
        try {
            if (commandArguments.getArguments().length != expectedNumberOfArguments) {
                MessageHolder.putCurrentCommand(commandArguments.getCommandName(), MessageType.USER_ERROR);
                throw new WrongAmountOfArgumentsException("Wrong amount of arguments: ",
                        commandArguments.getArguments().length, expectedNumberOfArguments);
            }
            return true;
        } catch (WrongAmountOfArgumentsException e) {
            MessageHolder.putMessage(e.getMessage(), MessageType.USER_ERROR);
        }
        return false;
    }

    private boolean validateDistanceTravelled(CommandArguments commandArguments) {
        String commandName = commandArguments.getCommandName();
        String[] arguments = commandArguments.getArguments();
        if (!checkNumberOfArguments(commandArguments, 1))
            return false;
        CheckingResult checkingResult = ValueHandler.DISTANCE_TRAVELLED_CHECKER.check(arguments[0]);
        if (!checkingResult.getStatus()) {
            MessageHolder.putCurrentCommand(commandName + " " + arguments[0], MessageType.USER_ERROR);
            MessageHolder.putMessage(checkingResult.getMessage(), MessageType.USER_ERROR);
            return false;
        }
        return true;
    }

    private boolean validateEnginePower(CommandArguments commandArguments) {
        String[] arguments = commandArguments.getArguments();
        if (!checkNumberOfArguments(commandArguments, 1))
            return false;
        CheckingResult checkingResult = ValueHandler.ENGINE_POWER_CHECKER.check(arguments[0]);
        if (!checkingResult.getStatus()) {
            MessageHolder.putCurrentCommand(RemoveAllByEnginePowerCommand.getName() + " " +
                    arguments[0], MessageType.USER_ERROR);
            MessageHolder.putMessage(checkingResult.getMessage(), MessageType.USER_ERROR);
            return false;
        }
        return true;
    }
    /**
     * Checks the arguments of commands that use the key as an argument.
     * @param commandArguments
     * @return Check status.
     */
    private boolean validateKey(CommandArguments commandArguments) {
        String[] arguments = commandArguments.getArguments();
        String commandName = commandArguments.getCommandName();
        if (arguments.length == 0) {
            MessageHolder.putCurrentCommand(commandName, MessageType.USER_ERROR);
            MessageHolder.putMessage("Key value cannot be null", MessageType.USER_ERROR);
            return false;
        }
        if (!checkNumberOfArguments(commandArguments, 1))
            return false;
        IdentifierHandler identifierHandler = new IdentifierHandler(new Hashtable<>());//////////////
        if (!identifierHandler.checkKey(arguments[0], commandName + " " + arguments[0]))
            return false;
        return true;
    }

    private boolean validateFuelType(CommandArguments commandArguments) {
        String[] arguments = commandArguments.getArguments();
        if (!checkNumberOfArguments(commandArguments, 1))
            return false;
        CheckingResult checkingResult = ValueHandler.FUEL_TYPE_CHECKER.check(arguments[0]);
        if (!checkingResult.getStatus()) {
            MessageHolder.putCurrentCommand(CountByFuelTypeCommand.getName() + " " +
                    arguments[0], MessageType.USER_ERROR);
            MessageHolder.putMessage(checkingResult.getMessage(), MessageType.USER_ERROR);
            return false;
        }
        return true;
    }

    private boolean validateId(CommandArguments commandArguments) {
        String[] arguments = commandArguments.getArguments();
        if (arguments.length == 0) {
            MessageHolder.putCurrentCommand(commandArguments.getCommandName(), MessageType.USER_ERROR);
            MessageHolder.putMessage("id value cannot be null", MessageType.USER_ERROR);
            return false;
        }
        if (!checkNumberOfArguments(commandArguments, 1))
            return false;
        IdentifierHandler identifierHandler = new IdentifierHandler(new Hashtable<>());//////////////
        if (!identifierHandler.checkId(arguments[0], UpdateCommand.getName() + " " + arguments[0]))
            return false;
        return true;
    }

    private boolean validateExecuteScriptCommand(CommandArguments commandArguments) {
        String[] arguments = commandArguments.getArguments();
        if (commandArguments.getExecuteMode() == ExecuteMode.COMMAND_MODE)
            scriptCounter.clear();
        if (!checkNumberOfArguments(commandArguments, 1))
            return false;
        File scriptFile = FileHandler.findFile(new File("scripts"), arguments[0]);
        if (scriptFile == null) {
            MessageHolder.putMessage(String.format(
                    "Script '%s' not found in 'scripts' directory", arguments[0]), MessageType.USER_ERROR);
            return false;
        }
        commandArguments.setScriptFile(scriptFile);
        if (scriptCounter.contains(scriptFile.getAbsolutePath())) {
            MessageHolder.putMessage(String.format("Command '%s %s':",
                    ExecuteScriptCommand.getName(), scriptFile.getName()), MessageType.USER_ERROR);
            MessageHolder.putMessage(String.format(
                    "Recursion on '%s' script noticed", scriptFile.getName()), MessageType.USER_ERROR);
            return false;
        }
        scriptCounter.add(scriptFile.getAbsolutePath());
        return true;
    }

    public boolean validate(CommandArguments commandArguments) {
        if (commandArguments == null)
            return false;
        if (answerType == AnswerType.EXECUTION_RESPONSE)
            return validateArguments(commandArguments);
        return validateExtraArguments(commandArguments);
    }

    private boolean validateArguments(CommandArguments commandArguments) {
        boolean isCorrect;
        switch (commandArguments.getCommandName()) {
            case "help", "info", "show", "clear", "exit" -> isCorrect = checkNumberOfArguments(commandArguments, 0);
            case "insert", "remove_key", "remove_greater_key" -> isCorrect = validateKey(commandArguments);
            case "update" -> isCorrect = validateId(commandArguments);
            case "execute_script" -> isCorrect = validateExecuteScriptCommand(commandArguments);
            case "remove_greater", "remove_lower" -> isCorrect = validateDistanceTravelled(commandArguments);
            case "remove_all_by_engine_power" -> isCorrect = validateEnginePower(commandArguments);
            case "count_by_fuel_type", "filter_less_than_fuel_type" -> isCorrect = validateFuelType(commandArguments);
            default -> {
                MessageHolder.putMessage(String.format(
                        "'%s': No such command", commandArguments.getCommandName()), MessageType.USER_ERROR);
                isCorrect = false;
            }
        }
        return isCorrect;
    }

    private boolean validateExtraArguments(CommandArguments commandArguments) {

        return true;
    }

}
