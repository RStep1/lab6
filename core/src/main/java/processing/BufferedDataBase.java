package processing;

import data.FuelType;
import data.Vehicle;

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
    private final Set<String> scriptCounter = new HashSet<>();
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
     * Displays information about all commands.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean help(CommandArguments commandArguments) {
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
        if (identifierHandler.hasElementWithKey(commandArguments.getArguments()[0], true,
                    InsertCommand.getName() + " " + commandArguments.getArguments()[0]))
                return false;
        if (commandArguments.getExtraArguments() == null)
            return true;
        return addElementBy(commandArguments, AddMode.INSERT_MODE);
    }

    /**
     * Updates the collection element by id.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean update(CommandArguments commandArguments) {
        if (!identifierHandler.hasElementWithId(Long.parseLong(commandArguments.getArguments()[0]))) {
            MessageHolder.putMessage("No such element with this id", MessageType.USER_ERROR);
            return false;
        }
        if (commandArguments.getExtraArguments() == null)
            return true;
        return addElementBy(commandArguments, AddMode.UPDATE_MODE);
    }

    /**
     * Executes 'insert' or 'update' command.
     * @param commandArguments
     * @param addMode Defines command.
     * @return Command exit status.
     */
    private boolean addElementBy(CommandArguments commandArguments, AddMode addMode) {
        String[] arguments = commandArguments.getArguments();
        String[] vehicleValues = commandArguments.getExtraArguments();
        String commandName = commandArguments.getCommandName();
        ExecuteMode executeMode = commandArguments.getExecuteMode();
        java.time.ZonedDateTime creationDate = ZonedDateTime.now();
        long key = 0, id = 0;
        if (addMode == AddMode.INSERT_MODE) {
            key = Long.parseLong(arguments[0]);
            id = identifierHandler.generateId();
        } else {
            id = Long.parseLong(arguments[0]);
            key = identifierHandler.getKeyById(id);
        }
        if (executeMode == ExecuteMode.SCRIPT_MODE && vehicleValues.length != Vehicle.getCountOfChangeableFields()) {
            MessageHolder.putCurrentCommand(commandName + " " + arguments[0], MessageType.USER_ERROR);
            MessageHolder.putMessage(String.format(
                    "There are not enough lines in script '%s' for the '%s %s' command",
                    commandArguments.getScriptFile().getName(), commandName, arguments[0]), MessageType.USER_ERROR);
            return false;
        }
        if (executeMode == ExecuteMode.SCRIPT_MODE && 
            !ValueHandler.checkValues(vehicleValues, commandName + " " + arguments[0])) {
            return false;
        }
        Vehicle vehicle = ValueHandler.getVehicle(id, creationDate, vehicleValues);
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
        FileHandler.saveDataBase(dataBase);
        // MessageHolder.putCurrentCommand(SaveCommand.getName(), MessageType.OUTPUT_INFO);
        // MessageHolder.putMessage("Collection successfully saved", MessageType.OUTPUT_INFO);
        System.out.println("Command " + SaveCommand.getName());
        System.out.println("Collection successfully saved");
        lastSaveTime = LocalDateTime.now();
        return true;
    }

    /**
     * Terminates a program or exits an executing script.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean exit(CommandArguments commandArguments) {
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
