package processing;

import data.FuelType;
import data.Vehicle;
import exceptions.WrongAmountOfArgumentsException;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import commands.*;
import mods.*;
import utility.*;

/**
 * Stores a database that can be manipulated in real time using a commands.
 * All commands implemented here.
 */
public class BufferedDataBase {
    private final Hashtable<Long, Vehicle> dataBase;
    private Set<String> scriptCounter = new HashSet<>();
    private CommandInvoker commandInvoker;
    private LocalDateTime lastInitTime;
    private LocalDateTime lastSaveTime;
    private final IdentifierHandler identifierHandler;
    private static final String datePattern = "dd/MM/yyy - HH:mm:ss";
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);

    public BufferedDataBase() {
        dataBase = FileHandler.loadDataBase();
        identifierHandler = new IdentifierHandler(dataBase);
        lastInitTime = dataBase.isEmpty() && lastInitTime == null ? null : LocalDateTime.now();
    }

    public void setCommandInvoker(CommandInvoker commandInvoker) {
        this.commandInvoker = commandInvoker;
    }

    /**
     * Checks each command for the correct number of arguments entered by the user.
     * @param arguments Arguments which entered on the same line as the command.
     * @param expectedNumberOfArguments Correct number of command arguments.
     * @return Check status.
     */
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
     * Checks the arguments of commands that use the key as an argument.
     * @param arguments Arguments which entered on the same line as the command.
     * @return Check status.
     */
    private boolean checkCommandWithKey(String[] arguments, String commandName) {
        if (arguments.length == 0) {
            MessageHolder.putCurrentCommand(commandName, MessageType.USER_ERROR);
            MessageHolder.putMessage("Key value cannot be null", MessageType.USER_ERROR);
            return false;
        }
        if (!checkNumberOfArguments(arguments, 1, commandName + " " + arguments[0]))
            return false;
        if (!identifierHandler.checkKey(arguments[0], commandName + " " + arguments[0]))
            return false;
        return true;
    }

    /**
     * Displays information about all commands.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean help(CommandArguments commandArguments) {
        if (!checkNumberOfArguments(commandArguments.getArguments(), 0, HelpCommand.getName()))
            return false;
        MessageHolder.putCurrentCommand(HelpCommand.getName(), MessageType.OUTPUT_INFO);
        MessageHolder.putMessage(FileHandler.readFile(FileType.REFERENCE), MessageType.OUTPUT_INFO);
        return true;
    }

    /**
     * Displays information about collection.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean info(CommandArguments commandArguments) {
        if (!checkNumberOfArguments(commandArguments.getArguments(), 0, InfoCommand.getName()))
            return false;
        String stringLastInitTime = (lastInitTime == null ?
                "there have been no initializations in this session yet" : lastInitTime.format(dateFormatter));
        String stringLastSaveTime = (lastSaveTime == null ?
                "there hasn't been a save here yet" : lastSaveTime.format(dateFormatter));
        MessageHolder.putCurrentCommand(InfoCommand.getName(), MessageType.OUTPUT_INFO);
        MessageHolder.putMessage(String.format("""
                Information about collection:
                Type of collection:  %s
                Initialization date: %s
                Last save time:      %s
                Number of elements:  %s""", getCollectionType(), stringLastInitTime,
                stringLastSaveTime, getCollectionSize()), MessageType.OUTPUT_INFO);
        return true;
    }

    /**
     * Displays all elements of the collection.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean show(CommandArguments commandArguments) {
        if (!checkNumberOfArguments(commandArguments.getArguments(), 0, ShowCommand.getName()))
            return false;
        MessageHolder.putCurrentCommand(ShowCommand.getName(), MessageType.OUTPUT_INFO);
        if (dataBase.isEmpty()) {
            MessageHolder.putMessage("Collection is empty", MessageType.OUTPUT_INFO);
           return true;
        }
        TreeMap<Long, Vehicle> treeMapData = new TreeMap<>(dataBase);
        treeMapData.keySet().forEach(key ->
                MessageHolder.putMessage("key:                " + key +
                        "\n" + treeMapData.get(key) + "", MessageType.OUTPUT_INFO));
        return true;
    }

    /**
     * Adds a new element to the collection by key.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean insert(CommandArguments commandArguments) {
        return addElementBy(commandArguments.getArguments(), commandArguments.getExtraArguments(),
                commandArguments.getExecuteMode(), AddMode.INSERT_MODE, InsertCommand.getName());
    }

    /**
     * Updates the collection element by id.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean update(CommandArguments commandArguments) {
        return addElementBy(commandArguments.getArguments(), commandArguments.getExtraArguments(),
                commandArguments.getExecuteMode(), AddMode.UPDATE_MODE, UpdateCommand.getName());
    }

    /**
     * Executes 'insert' or 'update' command.
     * @param arguments Arguments which entered on the same line as the command.
     * @param vehicleValues Arguments for commands that make changes to database elements.
     * @param addMode Defines command.
     * @return Command exit status.
     */
    private boolean addElementBy(String[] arguments, String[] vehicleValues,
                                 ExecuteMode executeMode, AddMode addMode, String commandName) {
        if (arguments.length == 0) {
            MessageHolder.putCurrentCommand(commandName, MessageType.USER_ERROR);
            MessageHolder.putMessage(String.format(
                    "%s value cannot be null", addMode.getValueName()), MessageType.USER_ERROR);
            return false;
        }
        if (!checkNumberOfArguments(arguments, 1, commandName))
            return false;
        java.time.ZonedDateTime creationDate = ZonedDateTime.now();
        long key = 0;
        long id = 0;
        switch (addMode) {
            case INSERT_MODE -> {
                if (!identifierHandler.checkKey(arguments[0], InsertCommand.getName() + " " + arguments[0]))
                    return false;
                if (identifierHandler.hasElementWithKey(arguments[0], true,
                        InsertCommand.getName() + " " + arguments[0]))
                    return false;
                key = Long.parseLong(arguments[0]);
                id = identifierHandler.generateId();
            }
            case UPDATE_MODE -> {
                if (!identifierHandler.checkId(arguments[0], UpdateCommand.getName() + " " + arguments[0]))
                    return false;
                id = Long.parseLong(arguments[0]);
                key = identifierHandler.getKeyById(id);
            }
            default -> System.err.printf("Command %s: No suitable add mode file%n", commandName);
        }
        Vehicle vehicle;
        if (executeMode == ExecuteMode.COMMAND_MODE)
            vehicle = Console.insertMode(id, creationDate);
        else {
            if (vehicleValues.length != Vehicle.getCountOfChangeableFields()) {
                MessageHolder.putCurrentCommand(commandName + " " + arguments[0], MessageType.USER_ERROR);
                MessageHolder.putMessage(String.format(
                        "There are not enough lines in script for the '%s %s' command",
                        commandName, arguments[0]), MessageType.USER_ERROR);
                return false;
            }
            if (!ValueHandler.checkValues(vehicleValues, commandName + " " + arguments[0]))
                return false;
            vehicle = ValueHandler.getVehicle(id, creationDate, vehicleValues);
        }
        dataBase.put(key, vehicle);
        MessageHolder.putCurrentCommand(commandName + " " + arguments[0], MessageType.OUTPUT_INFO);
        MessageHolder.putMessage("Element was successfully " + addMode.getResultMessage(), MessageType.OUTPUT_INFO);
        return true;
    }

    /**
     * Removes element by key.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeKey(CommandArguments commandArguments) {
        String[] arguments = commandArguments.getArguments();
        if (!checkCommandWithKey(arguments, RemoveKeyCommand.getName()))
            return false;
        if (!identifierHandler.hasElementWithKey(arguments[0], false,
                RemoveKeyCommand.getName() + " " + arguments[0]))
            return false;
        long key = Long.parseLong(arguments[0]);
        dataBase.remove(key);
        MessageHolder.putCurrentCommand(RemoveKeyCommand.getName() + " " + arguments[0], MessageType.OUTPUT_INFO);
        MessageHolder.putMessage(String.format(
                "Element with key = %s was successfully removed", key), MessageType.OUTPUT_INFO);
        return true;
    }

    /**
     * Clears the collection.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean clear(CommandArguments commandArguments) {
        if (!checkNumberOfArguments(commandArguments.getArguments(), 0, ClearCommand.getName()))
            return false;
        MessageHolder.putCurrentCommand(ClearCommand.getName(), MessageType.OUTPUT_INFO);
        if (dataBase.isEmpty()) {
            MessageHolder.putMessage("Collection is already empty", MessageType.OUTPUT_INFO);
        } else {
            dataBase.clear();
            MessageHolder.putMessage("Collection successfully cleared", MessageType.OUTPUT_INFO);
        }
        return true;
    }

    /**
     * Saves the collection to a Json file.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean save(CommandArguments commandArguments) {
        if (!checkNumberOfArguments(commandArguments.getArguments(), 0, SaveCommand.getName()))
            return false;
        FileHandler.saveDataBase(dataBase);
        MessageHolder.putCurrentCommand(SaveCommand.getName(), MessageType.OUTPUT_INFO);
        MessageHolder.putMessage("Collection successfully saved", MessageType.OUTPUT_INFO);
        lastSaveTime = LocalDateTime.now();
        return true;
    }

    /**
     * Executes user script.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean executeScript(CommandArguments commandArguments) {
        String[] arguments = commandArguments.getArguments();
        if (commandArguments.getExecuteMode() == ExecuteMode.COMMAND_MODE)
            scriptCounter.clear();
        if (!checkNumberOfArguments(arguments, 1, ExecuteScriptCommand.getName()))
            return false;
        File scriptFile = FileHandler.findFile(new File("scripts"), arguments[0]);
        if (scriptFile == null) {
            MessageHolder.putMessage(String.format(
                    "Script '%s' not found in 'scripts' directory", arguments[0]), MessageType.USER_ERROR);
            return false;
        }
        if (scriptCounter.contains(scriptFile.getAbsolutePath())) {
            MessageHolder.putMessage(String.format("Command '%s %s':",
                    ExecuteScriptCommand.getName(), scriptFile.getName()), MessageType.USER_ERROR);
            MessageHolder.putMessage(String.format(
                    "Recursion on '%s' script noticed", scriptFile.getName()), MessageType.USER_ERROR);
            return false;
        }
        scriptCounter.add(scriptFile.getAbsolutePath());
        MessageHolder.putCurrentCommand(
                ExecuteScriptCommand.getName() + " " + scriptFile.getName(), MessageType.OUTPUT_INFO);
        ArrayList<String> scriptLines = FileHandler.readScriptFile(scriptFile);
        if (scriptLines.isEmpty()) {
            MessageHolder.putMessage(String.format(
                    "Script '%s' is empty", scriptFile.getName()), MessageType.OUTPUT_INFO);
            return true;
        }
        CommandParser commandParser = new CommandParser(commandInvoker, scriptLines);
        return commandParser.scriptProcessing(scriptFile.getName());
    }


    /**
     * Terminates a program or exits an executing script.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean exit(CommandArguments commandArguments) {
        if (!checkNumberOfArguments(commandArguments.getArguments(), 0, ExitCommand.getName()))
            return false;
        MessageHolder.putCurrentCommand(ExitCommand.getName(), MessageType.OUTPUT_INFO);
        if (commandArguments.getExecuteMode() == ExecuteMode.COMMAND_MODE)
            MessageHolder.putMessage("Program successfully completed", MessageType.OUTPUT_INFO);
        return true;
    }

    /**
     * Removes all elements of the collection whose distance travelled value exceeds the given value.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeGreater(CommandArguments commandArguments) {
        return removeAllByDistanceTravelled(commandArguments, RemoveGreaterCommand.getName(), RemoveMode.REMOVE_GREATER);
    }

    /**
     * Removes all elements of the collection whose distance travelled value is less than the given value.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeLower(CommandArguments commandArguments) {
        return removeAllByDistanceTravelled(commandArguments, RemoveLowerCommand.getName(), RemoveMode.REMOVE_LOWER);
    }

    /**
     * Executes 'remove greater' or 'remove_lower' command.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @param removeMode Defines command.
     * @return Command exit status.
     */
    private boolean removeAllByDistanceTravelled(CommandArguments commandArguments,
                                                 String commandName, RemoveMode removeMode) {
        String[] arguments = commandArguments.getArguments();
        if (!checkNumberOfArguments(arguments, 1, commandName))
            return false;
        CheckingResult checkingResult = ValueHandler.DISTANCE_TRAVELLED_CHECKER.check(arguments[0]);
        if (!checkingResult.getStatus()) {
            MessageHolder.putCurrentCommand(commandName + " " + arguments[0], MessageType.USER_ERROR);
            MessageHolder.putMessage(checkingResult.getMessage(), MessageType.USER_ERROR);
            return false;
        }
        long userDistanceTravelled = Long.parseLong(arguments[0]);
        Set<Long> filteredKeys = dataBase.keySet().stream()
                .filter(key -> (removeMode == RemoveMode.REMOVE_GREATER ?
                        dataBase.get(key).getDistanceTravelled() > userDistanceTravelled :
                        dataBase.get(key).getDistanceTravelled() < userDistanceTravelled))
                        .collect(Collectors.toSet());
        int countOfRemoved = 0;
        for (Long key : filteredKeys) {
            dataBase.remove(key);
            countOfRemoved++;
        }
        MessageHolder.putCurrentCommand(commandName, MessageType.OUTPUT_INFO);
        if (countOfRemoved == 0) {
            MessageHolder.putMessage(String.format(
                    "No elements found to remove with distance travelled %s %s",
                    removeMode.getSymbol(), userDistanceTravelled), MessageType.OUTPUT_INFO);
        } else {
            MessageHolder.putMessage(String.format(
                    "%s elements were successfully removed with distance travelled %s %s",
                    countOfRemoved, removeMode.getSymbol(), userDistanceTravelled), MessageType.OUTPUT_INFO);
        }
        return true;
    }

    /**
     * Removes all elements of the collection whose key is greater than the given value.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeGreaterKey(CommandArguments commandArguments) {
        String[] arguments = commandArguments.getArguments();
        if (!checkCommandWithKey(arguments, RemoveGreaterKeyCommand.getName()))
            return false;
        long userKey = Long.parseLong(arguments[0]);
        int countOfRemovedKeys = 0;
        Set<Long> filteredKeys = dataBase.keySet().stream().filter(key -> key > userKey).collect(Collectors.toSet());
        for (Long key : filteredKeys) {
            dataBase.remove(key);
            countOfRemovedKeys++;
        }
        MessageHolder.putCurrentCommand(RemoveGreaterKeyCommand.getName(), MessageType.OUTPUT_INFO);
        if (countOfRemovedKeys == 0)
            MessageHolder.putMessage("No matching keys to remove element", MessageType.OUTPUT_INFO);
        else
            MessageHolder.putMessage(
                    String.format("%s elements was successfully removed", countOfRemovedKeys), MessageType.OUTPUT_INFO);
        return true;
    }

    /**
     * Removes all elements in the collection whose engine power is equal to the given value.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeAllByEnginePower(CommandArguments commandArguments) {
        String[] arguments = commandArguments.getArguments();
        if (!checkNumberOfArguments(arguments, 1, RemoveAllByEnginePowerCommand.getName()))
            return false;
        CheckingResult checkingResult = ValueHandler.ENGINE_POWER_CHECKER.check(arguments[0]);
        if (!checkingResult.getStatus()) {
            MessageHolder.putCurrentCommand(RemoveAllByEnginePowerCommand.getName() + " " +
                    arguments[0], MessageType.USER_ERROR);
            MessageHolder.putMessage(checkingResult.getMessage(), MessageType.USER_ERROR);
            return false;
        }
        int userEnginePower = Integer.parseInt(arguments[0]);
        int countOfRemoved = 0;
        Set<Long> filteredKeys = dataBase.keySet().stream()
                .filter(key -> dataBase.get(key).getEnginePower() == userEnginePower)
                .collect(Collectors.toSet());
        for (Long key : filteredKeys) {
            dataBase.remove(key);
            countOfRemoved++;
        }
        MessageHolder.putCurrentCommand(RemoveAllByEnginePowerCommand.getName(), MessageType.OUTPUT_INFO);
        if (countOfRemoved == 0)
            MessageHolder.putMessage(String.format(
                    "No elements found to remove with engine power = %s", userEnginePower), MessageType.OUTPUT_INFO);
        else
            MessageHolder.putMessage(String.format(
                    "%s elements were successfully removed with engine power = %s",
                    countOfRemoved, userEnginePower), MessageType.OUTPUT_INFO);
        return true;
    }

    /**
     * Prints the number of elements in the collection whose fuel type is equal to the given value.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean countByFuelType(CommandArguments commandArguments) {
        String[] arguments = commandArguments.getArguments();
        if (!checkNumberOfArguments(arguments, 1, CountByFuelTypeCommand.getName()))
            return false;
        CheckingResult checkingResult = ValueHandler.FUEL_TYPE_CHECKER.check(arguments[0]);
        if (!checkingResult.getStatus()) {
            MessageHolder.putCurrentCommand(CountByFuelTypeCommand.getName() + " " +
                    arguments[0], MessageType.USER_ERROR);
            MessageHolder.putMessage(checkingResult.getMessage(), MessageType.USER_ERROR);
            return false;
        }
        FuelType fuelType = ValueTransformer.SET_FUEL_TYPE.apply(
                ValueHandler.TYPE_CORRECTION.correct(arguments[0]));
        long count = dataBase.keySet().stream()
                .filter(key -> fuelType.equals(dataBase.get(key).getFuelType()))
                .count();
        MessageHolder.putCurrentCommand(CountByFuelTypeCommand.getName(), MessageType.OUTPUT_INFO);
        MessageHolder.putMessage(String.format("%s elements with fuel type = %s (%s)",
                count, fuelType.getSerialNumber(), fuelType), MessageType.OUTPUT_INFO);
        return true;
    }

    /**
     * Prints all elements of the collection whose fuel type is less than or equal to the given value.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean filterLessThanFuelType(CommandArguments commandArguments) {
        String[] arguments = commandArguments.getArguments();
        if (!checkNumberOfArguments(arguments, 1, FilterLessThanFuelTypeCommand.getName()))
            return false;
        CheckingResult checkingResult = ValueHandler.FUEL_TYPE_CHECKER.check(arguments[0]);
        if (!checkingResult.getStatus()) {
            MessageHolder.putCurrentCommand(FilterLessThanFuelTypeCommand.getName() + " " +
                    arguments[0], MessageType.USER_ERROR);
            MessageHolder.putMessage(checkingResult.getMessage(), MessageType.USER_ERROR);
            return false;
        }
        FuelType fuelType = ValueTransformer.SET_FUEL_TYPE.apply(
                ValueHandler.TYPE_CORRECTION.correct(arguments[0]));
        AtomicBoolean hasSuchElements = new AtomicBoolean(false);
        MessageHolder.putCurrentCommand(FilterLessThanFuelTypeCommand.getName(), MessageType.OUTPUT_INFO);
        TreeMap<Long, Vehicle> treeMapData = new TreeMap<>(dataBase);
        treeMapData.keySet().stream()
                .filter(key -> treeMapData.get(key).getFuelType().getSerialNumber() <= fuelType.getSerialNumber())
                .forEach(key -> {
                    MessageHolder.putMessage("key:                " + key +
                        "\n" + treeMapData.get(key) + "", MessageType.OUTPUT_INFO);
                    hasSuchElements.set(true);
                });
        if (!hasSuchElements.get()) {
            MessageHolder.putMessage(String.format(
                    "No elements found with fuel type value less than %s (%s)",
                    fuelType.getSerialNumber(), fuelType), MessageType.OUTPUT_INFO);
        }
        return true;
    }

    public String getCollectionType() {
        return dataBase.getClass().getName();
    }

    public int getCollectionSize() {
        return dataBase.size();
    }
}
