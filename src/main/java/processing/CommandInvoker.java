package processing;

import commands.*;
import mods.ExecuteMode;
import utility.CommandArguments;
import utility.FileHandler;

import java.util.ArrayList;

/**
 * Calls each wrapper command.
 */
public class CommandInvoker {
    private ArrayList<Command> commandList = new ArrayList<>();

    /**
     * Initializes each command and set reference file.
     */
    public CommandInvoker(Command helpCommand, Command infoCommand, Command showCommand,
                          Command insertCommand, Command updateCommand, Command removeKeyCommand,
                          Command clearCommand, Command saveCommand, Command executeScriptCommand,
                          Command exitCommand, Command removeGreaterCommand, Command removeLowerCommand,
                          Command removeGreaterKeyCommand, Command removeAllByEnginePowerCommand,
                          Command countByFuelTypeCommand, Command filterLessThanFuelTypeCommand) {
        commandList.add(helpCommand);
        commandList.add(infoCommand);
        commandList.add(showCommand);
        commandList.add(insertCommand);
        commandList.add(updateCommand);
        commandList.add(removeKeyCommand);
        commandList.add(clearCommand);
        commandList.add(saveCommand);
        commandList.add(executeScriptCommand);
        commandList.add(exitCommand);
        commandList.add(removeGreaterCommand);
        commandList.add(removeLowerCommand);
        commandList.add(removeGreaterKeyCommand);
        commandList.add(removeAllByEnginePowerCommand);
        commandList.add(countByFuelTypeCommand);
        commandList.add(filterLessThanFuelTypeCommand);

        setReferenceFile();
    }

    /**
     * Fills the reference file.
     */
    private void setReferenceFile() {
        StringBuilder reference = new StringBuilder();
        for (Command command : commandList)
            reference.append(command).append("\n");
        FileHandler.writeReferenceFile(reference.toString());
    }

    /**
     * Invokes the 'help' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean help(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof HelpCommand helpCommand) {
                return helpCommand.execute(commandArguments);
            }
        }
        return false;
    }

    /**
     * Invokes the 'info' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean info(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof InfoCommand infoCommand) {
                return infoCommand.execute(commandArguments);
            }
        }
        return false;
    }

    /**
     * Invokes the 'show' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean show(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof ShowCommand showCommand) {
                return showCommand.execute(commandArguments);
            }
        }
        return false;
    }

    /**
     * Invokes the 'insert' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean insert(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof InsertCommand insertCommand) {
                return insertCommand.execute(commandArguments);
            }
        }
        return false;
    }

    /**
     * Invokes the 'update' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean update(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof UpdateCommand updateCommand) {
                return updateCommand.execute(commandArguments);
            }
        }
        return false;
    }

    /**
     * Invokes the 'remove key' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeKey(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof RemoveKeyCommand removeKeyCommand) {
                return removeKeyCommand.execute(commandArguments);
            }
        }
        return false;
    }

    /**
     * Invokes the 'clear' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean clear(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof ClearCommand clearCommand) {
                return clearCommand.execute(commandArguments);
            }
        }
        return false;
    }

    /**
     * Invokes the 'save' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean save(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof SaveCommand saveCommand) {
                return saveCommand.execute(commandArguments);
            }
        }
        return false;
    }

    /**
     * Invokes the 'execute script' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean executeScript(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof ExecuteScriptCommand executeScriptCommand) {
                return executeScriptCommand.execute(commandArguments);
            }
        }
        return false;
    }

    /**
     * Invokes the 'help' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean exit(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof ExitCommand exitCommand) {
                return exitCommand.execute(commandArguments);
            }
        }
        return false;
    }

    /**
     * Invokes the 'remove greater' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeGreater(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof RemoveGreaterCommand removeGreaterCommand) {
                return removeGreaterCommand.execute(commandArguments);
            }
        }
        return false;
    }

    /**
     * Invokes the 'remove lower' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeLower(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof RemoveLowerCommand removeLowerCommand) {
                return removeLowerCommand.execute(commandArguments);
            }
        }
        return false;
    }

    /**
     * Invokes the 'remove greater key' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeGreaterKey(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof RemoveGreaterKeyCommand removeGreaterKeyCommand) {
                return removeGreaterKeyCommand.execute(commandArguments);
            }
        }
        return false;
    }

    /**
     * Invokes the 'remove all by engine power' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean removeAllByEnginePower(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof RemoveAllByEnginePowerCommand removeAllByEnginePowerCommand) {
                return removeAllByEnginePowerCommand.execute(commandArguments);
            }
        }
        return false;
    }

    /**
     * Invokes the 'count by fuel type' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean countByFuelType(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof CountByFuelTypeCommand countByFuelTypeCommand) {
                return countByFuelTypeCommand.execute(commandArguments);
            }
        }
        return false;
    }

    /**
     * Invokes the 'filter less than fuel type' command from its wrapper class.
     * @param commandArguments contains the name of the command, its arguments on a single line,
     *                        arguments that are characteristics of the collection class and execution mode.
     * @return Command exit status.
     */
    public boolean filterLessThanFuelType(CommandArguments commandArguments) {
        for (Command command : commandList) {
            if (command instanceof FilterLessThanFuelTypeCommand filterLessThanFuelTypeCommand) {
                return filterLessThanFuelTypeCommand.execute(commandArguments);
            }
        }
        return false;
    }
}
