package utility;

import mods.MessageType;

import java.util.ArrayList;
import java.util.List;

public class MessageHolder {
    private static final List<String> outputInfo = new ArrayList<>();
    private static final List<String> userErrors = new ArrayList<>();

    public static void putMessage(String message, MessageType messageType) {
        if (messageType == MessageType.OUTPUT_INFO)
            outputInfo.add(message);
        if (messageType == MessageType.USER_ERROR)
            userErrors.add(message);
    }

    public static String getMessages(MessageType messageType) {
        StringBuilder messages = new StringBuilder();
        if (messageType == MessageType.OUTPUT_INFO)
            outputInfo.forEach(line -> messages.append(line).append("\n"));
        if (messageType == MessageType.USER_ERROR)
            userErrors.forEach(line -> messages.append(line).append("\n"));
        return messages.toString();
    }

    public static void clearMessages(MessageType messageType) {
        if (messageType == MessageType.OUTPUT_INFO)
            outputInfo.clear();
        if (messageType == MessageType.USER_ERROR)
            userErrors.clear();
    }

    public static void putCurrentCommand(String commandName, MessageType messageType){
        String message = String.format("Command '%s':", commandName);
        putMessage(message, messageType);
    }
}
