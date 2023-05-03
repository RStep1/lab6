package utility;

import mods.MessageType;

import java.util.ArrayList;

public class MessageHolder implements Cloneable {
    private static final ArrayList<String> outputInfo = new ArrayList<>();
    private static final ArrayList<String> userErrors = new ArrayList<>();

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

    public static ArrayList<String> getOutputInfo() {
        return outputInfo;
    }

    public static ArrayList<String> getUserErrors() {
        return userErrors;
    }

    @Override
    public MessageHolder clone() {
        try {
            MessageHolder clone = (MessageHolder) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
