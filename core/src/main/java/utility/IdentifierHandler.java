package utility;

import data.Vehicle;
import exceptions.NoSuchIdException;
import mods.MessageType;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Pattern;

/**
 * Performs identifiers validation or generation.
 */
public class IdentifierHandler {
    Hashtable<Long, Vehicle> dataBase;
    private static final int ID_LENGTH = 10;
    private static final int MAX_KEY_LENGTH = 10;

    public IdentifierHandler(Hashtable<Long, Vehicle> dataBase) {
        this.dataBase = dataBase;
    }

    private boolean hasNonNumericCharacters(String value, String valueName, String commandName) {
        String nonDigitValue = "-?\\d+";
        Pattern nonDigitValuePattern = Pattern.compile(nonDigitValue);
        if (nonDigitValuePattern.matcher(value).matches()) {
            return false;
        }
        MessageHolder.putMessage(commandName, MessageType.USER_ERROR);
        MessageHolder.putMessage(String.format("%s must be a number", valueName), MessageType.USER_ERROR);
        return true;
    }

    private boolean isNegativeValue(String value, String valueName, String commandName) {
        String nonPositiveValue = "-\\d+";
        Pattern nonPositiveValuePattern = Pattern.compile(nonPositiveValue);
        if (nonPositiveValuePattern.matcher(value).matches()) {
            MessageHolder.putCurrentCommand(commandName, MessageType.USER_ERROR);
            MessageHolder.putMessage(String.format("%s cannot be negative", valueName), MessageType.USER_ERROR);
            return true;
        }
        return false;
    }

    private boolean hasLeadingZeros(String value, String valueName, String commandName) {
        String leadingZeros = "^0+\\d+";
        Pattern leadingZerosPattern = Pattern.compile(leadingZeros);
        if (leadingZerosPattern.matcher(value).matches()) {
            MessageHolder.putCurrentCommand(commandName, MessageType.USER_ERROR);
            MessageHolder.putMessage(String.format("%s cannot have leading zeros", valueName), MessageType.USER_ERROR);
            return true;
        }
        return false;
    }

    public boolean checkKey(String key, String commandName) {
        if (hasNonNumericCharacters(key, "Key", commandName))
            return false;
        if (isNegativeValue(key, "Key", commandName))
            return false;
        if (hasLeadingZeros(key, "key", commandName))
            return false;
        if (key.length() > MAX_KEY_LENGTH) {
            MessageHolder.putCurrentCommand(commandName, MessageType.USER_ERROR);
            MessageHolder.putMessage(String.format(
                    "Key is too long, max length - %s", MAX_KEY_LENGTH), MessageType.USER_ERROR);
            return false;
        }
        return true;
    }

    public boolean checkId(String id, String commandName) {
        if (hasNonNumericCharacters(id, "Id", commandName))
            return false;
        if (isNegativeValue(id, "Id", commandName))
            return false;
        if (hasLeadingZeros(id, "Id", commandName))
            return false;
        if (id.length() != ID_LENGTH) {
            MessageHolder.putCurrentCommand(commandName, MessageType.USER_ERROR);
            MessageHolder.putMessage(String.format(
                    "Invalid id length: %s, expected %s", id.length(), ID_LENGTH), MessageType.USER_ERROR);
            return false;
        }
        if (!hasElementWithId(Long.parseLong(id))) {
            MessageHolder.putCurrentCommand(commandName, MessageType.USER_ERROR);
            MessageHolder.putMessage("No such element with this id", MessageType.USER_ERROR);
            return false;
        }
        return true;
    }

    /**
     * Checks if the collection contains an element with the given id.
     */
    private boolean hasElementWithId(long id) {
        Enumeration<Long> keys = dataBase.keys();
        while (keys.hasMoreElements()) {
            Long key = keys.nextElement();
            if (id == dataBase.get(key).getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the collection contains an element with the given key.
     */
    public boolean hasElementWithKey(String key, boolean expectedResult, String commandName) {
        if (dataBase.containsKey(Long.parseLong(key))) {
            if (expectedResult) {
                MessageHolder.putCurrentCommand(commandName, MessageType.USER_ERROR);
                MessageHolder.putMessage("Element with such key already exists", MessageType.USER_ERROR);
            }
            return true;
        }
        if (!expectedResult) {
            MessageHolder.putCurrentCommand(commandName, MessageType.USER_ERROR);
            MessageHolder.putMessage("Element with such key not found", MessageType.USER_ERROR);
        }
        return false;
    }

    /**
     * Randomly fills in each digit of id until it becomes unique.
     * @return New unique id.
     */
    public Long generateId() {
        StringBuilder newId;
        do {
            newId = new StringBuilder(ID_LENGTH);
            newId.append((byte) (Math.random() * 9 + 1)); //skip leading zero
            for (int i = 1; i < ID_LENGTH; i++) {
                newId.append((byte) (Math.random() * 10));
            }
        } while (hasElementWithId(Long.parseLong(newId.toString())));
        return Long.parseLong(newId.toString());
    }

    /**
     * Finds the element with the given id.
     * @return Corresponding key.
     */
    public long getKeyById(long id) {
        long key = -1;
        Enumeration<Long> keys = dataBase.keys();
        while (keys.hasMoreElements()) {
            Long nextKey = keys.nextElement();
            if (id == dataBase.get(nextKey).getId()) {
                key = nextKey;
            }
        }
        if (key == -1) {
            RuntimeException e = new NoSuchIdException(id);
            System.err.println(e.getMessage());
            throw e;
        }
        return key;
    }
}
