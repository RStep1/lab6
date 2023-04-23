package mods;

/**
 * Enumeration of modes for commands that make changes to a collection element.
 */
public enum AddMode {
    INSERT_MODE("Key", "inserted"),
    UPDATE_MODE("Id", "updated");
    private final String valueName;
    private final String resultMessage;

    AddMode(String valueName, String resultMessage) {
        this.valueName = valueName;
        this.resultMessage = resultMessage;
    }

    /**
     * @return Returns the name of the value by which the element is changed.
     */
    public String getValueName() {
        return valueName;
    }

    /**
     * @return Returns part of the command completion message.
     */
    public String getResultMessage() {
        return resultMessage;
    }
}
