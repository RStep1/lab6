package processing;

import data.FuelType;
import data.Vehicle;
import exceptions.WrongAmountOfArgumentsException;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import commands.*;
import mods.AddMode;
import mods.ExecuteMode;
import mods.FileType;
import mods.RemoveMode;
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
                FileHandler.writeCurrentCommand(commandName, FileType.USER_ERRORS);
                throw new WrongAmountOfArgumentsException("Wrong amount of arguments: ",
                        arguments.length, expectedNumberOfArguments);
            }
            return true;
        } catch (WrongAmountOfArgumentsException e) {
            FileHandler.writeToFile(e.getMessage(), FileType.USER_ERRORS);
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
            FileHandler.writeCurrentCommand(commandName, FileType.USER_ERRORS);
            FileHandler.writeToFile("Key value cannot be null", FileType.USER_ERRORS);
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
        FileHandler.writeCurrentCommand(HelpCommand.getName(), FileType.OUTPUT);
        FileHandler.writeToFile(FileHandler.readFile(FileType.REFERENCE), FileType.OUTPUT);
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
        FileHandler.writeCurrentCommand(InfoCommand.getName(), FileType.OUTPUT);
        FileHandler.writeToFile(String.format("""
                Information about collection:
                Type of collection:  %s
                Initialization date: %s
                Last save time:      %s
                Number of elements:  %s""", getCollectionType(), stringLastInitTime,
                stringLastSaveTime, getCollectionSize()), FileType.OUTPUT);
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
        FileHandler.writeCurrentCommand(ShowCommand.getName(), FileType.OUTPUT);
        if (dataBase.isEmpty()) {
           FileHandler.writeToFile("Collection is empty", FileType.OUTPUT);
           return true;
        }
        TreeMap<Long, Vehicle> treeMapData = new TreeMap<>(dataBase);
        Set<Long> keys = treeMapData.keySet();
        for (Long key : keys) {
            FileHandler.writeToFile("key:                " + key +
                    "\n" + treeMapData.get(key) + "", FileType.OUTPUT);
        }
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
            FileHandler.writeCurrentCommand(commandName, FileType.USER_ERRORS);
            FileHandler.writeToFile(String.format(
                    "%s value cannot be null", addMode.getValueName()), FileType.USER_ERRORS);
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
            default -> FileHandler.writeToFile(String.format(
                    "Command %s: No suitable add mode file", commandName), FileType.SYSTEM_ERRORS);
        }
        Vehicle vehicle;
        if (executeMode == ExecuteMode.COMMAND_MODE)
            vehicle = Console.insertMode(id, creationDate);
        else {
            if (vehicleValues.length != Vehicle.getCountOfChangeableFields()) {
                FileHandler.writeCurrentCommand(commandName + " " + arguments[0], FileType.USER_ERRORS);
                FileHandler.writeToFile(String.format(
                        "There are not enough lines in script for the '%s %s' command",
                        commandName, arguments[0]), FileType.USER_ERRORS);
                return false;
            }
            if (!ValueHandler.checkValues(vehicleValues, commandName + " " + arguments[0]))
                return false;
            vehicle = ValueHandler.getVehicle(id, creationDate, vehicleValues);
        }
        dataBase.put(key, vehicle);
        FileHandler.writeCurrentCommand(commandName + " " + arguments[0], FileType.OUTPUT);
        FileHandler.writeToFile("Element was successfully " + addMode.getResultMessage(), FileType.OUTPUT);
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
        FileHandler.writeCurrentCommand(RemoveKeyCommand.getName() + " " + arguments[0], FileType.OUTPUT);
        FileHandler.writeToFile(String.format("Element with key = %s was successfully removed", key), FileType.OUTPUT);
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
        FileHandler.writeCurrentCommand(ClearCommand.getName(), FileType.OUTPUT);
        if (dataBase.isEmpty()) {
            FileHandler.writeToFile("Collection is already empty", FileType.OUTPUT);
        } else {
            dataBase.clear();
            FileHandler.writeToFile("Collection successfully cleared", FileType.OUTPUT);
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
        FileHandler.writeCurrentCommand(SaveCommand.getName(), FileType.OUTPUT);
        FileHandler.writeToFile("Collection successfully saved", FileType.OUTPUT);
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
            FileHandler.writeToFile(String.format(
                    "Script '%s' not found in 'scripts' directory", arguments[0]), FileType.USER_ERRORS);
            return false;
        }
        if (scriptCounter.contains(scriptFile.getAbsolutePath())) {
            FileHandler.writeToFile(String.format("Command '%s %s':",
                    ExecuteScriptCommand.getName(), scriptFile.getName()), FileType.USER_ERRORS);
            FileHandler.writeToFile(String.format(
                    "Recursion on '%s' script noticed", scriptFile.getName()), FileType.USER_ERRORS);
            return false;
        }
        scriptCounter.add(scriptFile.getAbsolutePath());
        FileHandler.writeCurrentCommand(ExecuteScriptCommand.getName() + " " + scriptFile.getName(), FileType.OUTPUT);
        ArrayList<String> scriptLines = FileHandler.readScriptFile(scriptFile);
        if (scriptLines.isEmpty()) {
            FileHandler.writeToFile(String.format("Script '%s' is empty", scriptFile.getName()), FileType.OUTPUT);
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
        FileHandler.writeCurrentCommand(ExitCommand.getName(), FileType.OUTPUT);
        if (commandArguments.getExecuteMode() == ExecuteMode.COMMAND_MODE)
            FileHandler.writeToFile("Program successfully completed", FileType.OUTPUT);
        return true;
    }

    /**
     * Removes all elements of the collection whose distance travelled value exceeds the given value.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeGreater(CommandArguments commandArguments) {
        return removeAllByDistanceTravelled(commandArguments.getArguments(), commandArguments.getExtraArguments(),
                commandArguments.getExecuteMode(), RemoveGreaterCommand.getName(), RemoveMode.REMOVE_GREATER);
    }

    /**
     * Removes all elements of the collection whose distance travelled value is less than the given value.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeLower(CommandArguments commandArguments) {
        return removeAllByDistanceTravelled(commandArguments.getArguments(), commandArguments.getExtraArguments(),
                commandArguments.getExecuteMode(), RemoveLowerCommand.getName(), RemoveMode.REMOVE_LOWER);
    }

    /**
     * Executes 'remove greater' or 'remove_lower' command.
     * @param arguments Arguments which entered on the same line as the command.
     * @param vehicleValues Arguments for commands that make changes to database elements.
     * @param removeMode Defines command.
     * @return Command exit status.
     */
    private boolean removeAllByDistanceTravelled(String[] arguments, String[] vehicleValues, ExecuteMode executeMode,
                                                 String commandName, RemoveMode removeMode) {
        if (!checkNumberOfArguments(arguments, 1, commandName))
            return false;
        CheckingResult checkingResult = ValueHandler.DISTANCE_TRAVELLED_CHECKER.check(arguments[0]);
        if (!checkingResult.getStatus()) {
            FileHandler.writeCurrentCommand(commandName + " " + arguments[0], FileType.USER_ERRORS);
            FileHandler.writeToFile(checkingResult.getMessage(), FileType.USER_ERRORS);
            return false;
        }
        long userDistanceTravelled = Long.parseLong(arguments[0]);

//        dataBase.keySet()
//                .stream()
//                .filter(key -> (removeMode == RemoveMode.REMOVE_GREATER ?
//                        dataBase.get(key).getDistanceTravelled() > userDistanceTravelled :
//                        dataBase.get(key).getDistanceTravelled() < userDistanceTravelled))
//                .forEach(dataBase::remove);

        Enumeration<Long> keys = dataBase.keys();
        int countOfRemoved = 0;
        while (keys.hasMoreElements()) {
            long nextKey = keys.nextElement();
            switch (removeMode) {
                case REMOVE_GREATER -> {
                    if (dataBase.get(nextKey).getDistanceTravelled() > userDistanceTravelled) {
                        dataBase.remove(nextKey);
                        countOfRemoved++;
                    }
                }
                case REMOVE_LOWER -> {
                    if (dataBase.get(nextKey).getDistanceTravelled() < userDistanceTravelled) {
                        dataBase.remove(nextKey);
                        countOfRemoved++;
                    }
                }
            }
        }
        FileHandler.writeCurrentCommand(commandName, FileType.OUTPUT);
        if (countOfRemoved == 0)
            FileHandler.writeToFile(String.format(
                    "No elements found to remove with distance travelled %s %s",
                    removeMode.getSymbol(), userDistanceTravelled), FileType.OUTPUT);
        else
            FileHandler.writeToFile(String.format(
                    "%s elements were successfully removed with distance travelled %s %s",
                    countOfRemoved, removeMode.getSymbol(), userDistanceTravelled), FileType.OUTPUT);
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
        Enumeration<Long> keys = dataBase.keys();
        int countOfRemovedKeys = 0;
        while (keys.hasMoreElements()) {
            long nextKey = keys.nextElement();
            if (nextKey > userKey) {
                dataBase.remove(nextKey);
                countOfRemovedKeys++;
            }
        }
        FileHandler.writeCurrentCommand(RemoveGreaterKeyCommand.getName(), FileType.OUTPUT);
        String message = "";
        if (countOfRemovedKeys == 0)
            message = "No matching keys to remove element";
        else
            message = String.format("%s elements was successfully removed", countOfRemovedKeys);
        FileHandler.writeToFile(message, FileType.OUTPUT);
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
            FileHandler.writeCurrentCommand(RemoveAllByEnginePowerCommand.getName() + " " +
                    arguments[0], FileType.USER_ERRORS);
            FileHandler.writeToFile(checkingResult.getMessage(), FileType.USER_ERRORS);
            return false;
        }
        int userEnginePower = Integer.parseInt(arguments[0]);
        int countOfRemoved = 0;
        Enumeration<Long> keys = dataBase.keys();
        while (keys.hasMoreElements()) {
            Long key = keys.nextElement();
            if (userEnginePower == dataBase.get(key).getEnginePower()) {
                dataBase.remove(key);
                countOfRemoved++;
            }
        }
        FileHandler.writeCurrentCommand(RemoveAllByEnginePowerCommand.getName(), FileType.OUTPUT);
        if (countOfRemoved == 0)
            FileHandler.writeToFile(String.format(
                    "No elements found to remove with engine power = %s", userEnginePower), FileType.OUTPUT);
        else
            FileHandler.writeToFile(String.format(
                    "%s elements were successfully removed with engine power = %s",
                    countOfRemoved, userEnginePower), FileType.OUTPUT);
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
            FileHandler.writeCurrentCommand(CountByFuelTypeCommand.getName() + " " +
                    arguments[0], FileType.USER_ERRORS);
            FileHandler.writeToFile(checkingResult.getMessage(), FileType.USER_ERRORS);
            return false;
        }
        FuelType fuelType = ValueTransformer.SET_FUEL_TYPE.apply(arguments[0]);
        int count = 0;
        Enumeration<Long> keys = dataBase.keys();
        while (keys.hasMoreElements()) {
            Long key = keys.nextElement();
            if (fuelType.equals(dataBase.get(key).getFuelType()))
                count++;
        }
        FileHandler.writeCurrentCommand(CountByFuelTypeCommand.getName(), FileType.OUTPUT);
        FileHandler.writeToFile(String.format(
                "%s elements with fuel type = %s (%s)", count, fuelType.getSerialNumber(), fuelType), FileType.OUTPUT);
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
            FileHandler.writeCurrentCommand(FilterLessThanFuelTypeCommand.getName() + " " +
                    arguments[0], FileType.USER_ERRORS);
            return false;
        }
        FuelType fuelType = ValueTransformer.SET_FUEL_TYPE.apply(arguments[0]);
        boolean hasSuchElements = false;
        FileHandler.writeCurrentCommand(FilterLessThanFuelTypeCommand.getName(), FileType.OUTPUT);
        TreeMap<Long, Vehicle> treeMapData = new TreeMap<>(dataBase);
        Set<Long> keys = treeMapData.keySet();
        for (Long key : keys) {
            if (treeMapData.get(key).getFuelType().getSerialNumber() <= fuelType.getSerialNumber()) {
                hasSuchElements = true;
                FileHandler.writeToFile("key:                " + key +
                        "\n" + treeMapData.get(key) + "", FileType.OUTPUT);
            }
        }
        if (!hasSuchElements)
            FileHandler.writeToFile(String.format(
                    "No elements found with fuel type value less than %s (%s)",
                    fuelType.getSerialNumber(), fuelType), FileType.OUTPUT);
        return true;
    }

    public String getCollectionType() {
        return dataBase.getClass().getName();
    }

    public int getCollectionSize() {
        return dataBase.size();
    }
}
