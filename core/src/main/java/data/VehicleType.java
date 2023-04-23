package data;

/**
 * Enumeration of vehicle types.
 * Serial number is used to simplify user input.
 */
public enum VehicleType {
    CAR(1),
    BOAT(2),
    CHOPPER(3),
    SPACESHIP(4);
    private final int serialNumber;

    VehicleType(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getSerialNumber() {
        return serialNumber;
    }
}
