package utility;

import data.Coordinates;
import data.FuelType;
import data.Vehicle;
import data.VehicleType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Contains functions for converting each string value to Vehicle class values.
 */
public class ValueTransformer {
    private static final String zonedDatePattern = "dd/MM/yyy - HH:mm:ss z";
    private static final DateTimeFormatter zonedDateFormatter = DateTimeFormatter.ofPattern(zonedDatePattern);

    private static final BiFunction<String, String, Coordinates> SET_COORDINATES = (newX, newY) -> {
        float x = Float.parseFloat(newX);
        double y = Double.parseDouble(newY);
        float truncatedX = BigDecimal.valueOf(x).setScale(Coordinates.getAccuracy(),
                RoundingMode.HALF_UP).floatValue();
        double truncatedY = BigDecimal.valueOf(y).setScale(Coordinates.getAccuracy(),
                RoundingMode.HALF_UP).doubleValue();
        return new Coordinates(truncatedX, truncatedY);
    };

    private static final Function<java.time.ZonedDateTime, String> SET_CREATION_DATE = (creationDateTime) ->
            creationDateTime.format(zonedDateFormatter);

    private static final Function<String, Integer> SET_ENGINE_POWER = Integer::parseInt;

    private static final Function<String, Long> SET_DISTANCE_TRAVELLED = Long::parseLong;

    private static final Function<String, VehicleType> SET_VEHICLE_TYPE = (newType) -> {
        VehicleType type = VehicleType.CAR;
        try {
            int serialNumber = Integer.parseInt(newType);
            for (VehicleType vehicleType : VehicleType.values())
                if (vehicleType.getSerialNumber() == serialNumber)
                    type = vehicleType;
        } catch (NumberFormatException e) {
            type = VehicleType.valueOf(newType);
        }
        return type;
    };

    public static final Function<String, FuelType> SET_FUEL_TYPE = (newFuelType) -> {
        FuelType fuelType = FuelType.ALCOHOL;
        try {
            int serialNumber = Integer.parseInt(newFuelType);
            for (FuelType type : FuelType.values())
                if (type.getSerialNumber() == serialNumber)
                    fuelType = type;
        } catch (NumberFormatException e) {
            fuelType = FuelType.valueOf(newFuelType);
        }
        return fuelType;
    };

    /**
     * Applies all the functions of a class to inline a new class.
     * @return New vehicle class.
     */
    public static Vehicle createVehicle(long id, String newName, String newX, String newY,
                                        java.time.ZonedDateTime creationDate, String newEnginePower,
                                        String newDistanceTravelled, String newVehicleType,
                                        String newFuelType) {
        return new Vehicle(id, newName, SET_COORDINATES.apply(newX, newY),
                SET_CREATION_DATE.apply(creationDate), SET_ENGINE_POWER.apply(newEnginePower),
                SET_DISTANCE_TRAVELLED.apply(newDistanceTravelled), SET_VEHICLE_TYPE.apply(newVehicleType),
                SET_FUEL_TYPE.apply(newFuelType));
    }
}
