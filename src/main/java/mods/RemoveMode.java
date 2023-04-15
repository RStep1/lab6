package mods;

/**
 *  Used to distinguish between two commands to remove an element.
 */
public enum RemoveMode {
    REMOVE_GREATER(">"),
    REMOVE_LOWER("<");
    private final String symbol;
    RemoveMode(String symbol) {
        this.symbol = symbol;
    }

    /**
     * @return Returns the mathematical symbol by which the elements are compared.
     */
    public String getSymbol() {
        return symbol;
    }
}
