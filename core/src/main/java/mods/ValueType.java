package mods;

/**
 * Used to determine the type of value in checkers.
 */
public enum ValueType {
    INTEGER("integer"),
    DOUBLE("double"),
    FLOAT("float"),
    LONG("long");
    private final String name;
    ValueType (String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
