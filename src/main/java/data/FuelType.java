package data;

/**
 * Enumeration of types of vehicle fuel.
 * Serial number is used to simplify user input.
 */
public enum FuelType {
    ALCOHOL(1),
    MANPOWER(2),
    NUCLEAR(3);
    private final int serialNumber;
    FuelType(int serialNumber) {
        this.serialNumber = serialNumber;
    }
    public int getSerialNumber() {
        return serialNumber;
    }
}
