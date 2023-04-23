package utility;


import data.Coordinates;
import data.FuelType;
import data.Vehicle;
import data.VehicleType;
import mods.MessageType;
import mods.ValueType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Contains correction and checking of all values of the collection class.
 */
public class ValueHandler {
    private static ArrayList<Process> valueProcesses = new ArrayList<>();

    /**
     * Populates and returns the list with processing of each value of collection class.
     */
    public static ArrayList<Process> getValueProcesses() {
        if (valueProcesses.isEmpty()) {
            valueProcesses.add(NAME_PROCESSING);
            valueProcesses.add(X_COORDINATE_PROCESSING);
            valueProcesses.add(Y_COORDINATE_PROCESSING);
            valueProcesses.add(ENGINE_POWER_PROCESSING);
            valueProcesses.add(DISTANCE_TRAVELLED_PROCESSING);
            valueProcesses.add(VEHICLE_TYPE_PROCESSING);
            valueProcesses.add(FUEL_TYPE_PROCESSING);
        }
        return valueProcesses;
    }

    /**
     * Checks a numeric value by its type
     * @return Boolean result and error message.
     */
    private static CheckingResult checkTypeValue(ValueType valueType, String value, String valueName) {
        Pattern valuePattern = Pattern.compile("");
        if (valueType == ValueType.DOUBLE || valueType == ValueType.FLOAT)
            valuePattern = Pattern.compile("([-+]?\\d*[.,]?\\d*)");
        if (valueType == ValueType.LONG || valueType == ValueType.INTEGER)
            valuePattern = Pattern.compile("[-+]?\\d+");
        if (valuePattern.matcher(value).matches() && !value.equals(""))
            return new CheckingResult(true, "");
        return new CheckingResult(false, String.format("%s must be of type %s", valueName, valueType.getName()));
    }

    private static final Correction NAME_CORRECTION = String::trim;
    private static final Correction NUMBER_VALUE_CORRECTION =
            value -> value.trim().replaceAll(",", ".").replaceAll("\\+", "");
    public static final Correction TYPE_CORRECTION = value -> value.trim().toUpperCase();
    private static final Checker NAME_CHECKER = name -> {
        if (name == null || name.equals("")) {
            return new CheckingResult(false, "Name cannot be null");
        }
        return new CheckingResult(true, "");
    };
    private static final Checker X_COORDINATE_CHECKER = newX -> {
        CheckingResult checkTypeValue = checkTypeValue(ValueType.FLOAT, newX, "X coordinate");
        if (!checkTypeValue.getStatus())
            return checkTypeValue;
        float x = Float.parseFloat(newX);
        try {
            float truncatedX = BigDecimal.valueOf(x).setScale(Coordinates.getAccuracy(),
                    RoundingMode.HALF_UP).floatValue();
            if (Float.compare(truncatedX, 341f) == 1) { // truncatedX > 341
                return new CheckingResult(false, "Max X value: 341");
            }
        } catch (NumberFormatException e) {
            return new CheckingResult(false, "Float value overflowed");
        }
        return new CheckingResult(true, "");
    };
    private static final Checker Y_COORDINATE_CHECKER = newY -> {
        CheckingResult checkTypeValue = checkTypeValue(ValueType.DOUBLE, newY, "Y coordinate");
        if (!checkTypeValue.getStatus())
            return checkTypeValue;
        double y = Double.parseDouble(newY);
        try {
            double truncatedY = BigDecimal.valueOf(y).setScale(Coordinates.getAccuracy(),
                    RoundingMode.HALF_UP).doubleValue();
            if (Double.compare(truncatedY, -272d) != 1) { // truncatedY <= 272
                return new CheckingResult(false, "Y must be grater than -272");
            }
        } catch (NumberFormatException e) {
            return new CheckingResult(false, "Double value overflowed");
        }
        return new CheckingResult(true, "");
    };
    public static final Checker ENGINE_POWER_CHECKER = newEnginePower -> {
        CheckingResult checkTypeValue = checkTypeValue(ValueType.INTEGER, newEnginePower, "Engine power");
        if (!checkTypeValue.getStatus())
            return checkTypeValue;
        try {
            int enginePower = Integer.parseInt(newEnginePower);
            if (enginePower <= 0) {
                return new CheckingResult(false, "Engine power must be greater than 0");
            }
        } catch (NumberFormatException e) {
            return new CheckingResult(false, "Integer value overflowed");
        }
        return new CheckingResult(true, "");
    };
    public static final Checker DISTANCE_TRAVELLED_CHECKER = newDistanceTravelled -> {
        CheckingResult checkTypeValue = checkTypeValue(ValueType.LONG, newDistanceTravelled, "Distance travelled");
        if (!checkTypeValue.getStatus())
            return checkTypeValue;
        try {
            long distanceTravelled = Long.parseLong(newDistanceTravelled);
            if (distanceTravelled <= 0) {
                return new CheckingResult(false, "Distance travelled must be greater than 0");
            }
        } catch (NumberFormatException e) {
            return new CheckingResult(false, "Long value overflowed");
        }
        return new CheckingResult(true, "");
    };
    private static final Checker VEHICLE_TYPE_CHECKER = newVehicleType -> {
        if (newVehicleType.equals("")) {
            return new CheckingResult(false, "Vehicle type cannot be null");
        }
        try {
            VehicleType.valueOf(newVehicleType);
        } catch (IllegalArgumentException illegalArgumentException) {
            try {
                int serialNumber = Integer.parseInt(newVehicleType);
                if (serialNumber < 1 || serialNumber > VehicleType.values().length) {
                    return new CheckingResult(false, "No such vehicle type number exits");
                }
            } catch (NumberFormatException numberFormatException) {
                return new CheckingResult(false, "This vehicle type does not exist");
            }
        }
        return new CheckingResult(true, "");
    };
    public static final Checker FUEL_TYPE_CHECKER = newFuelType -> {
        if (newFuelType.equals("")) {
            return new CheckingResult(false, "Fuel type cannot be null");
        }
        try {
            FuelType.valueOf(newFuelType.toUpperCase());
        } catch (IllegalArgumentException illegalArgumentException) {
            try {
                int serialNumber = Integer.parseInt(newFuelType);
                if (serialNumber < 1 || serialNumber > FuelType.values().length) {
                    return new CheckingResult(false, "No such fuel type number exits");
                }
            } catch (NumberFormatException numberFormatException) {
                return new CheckingResult(false, "This fuel type does not exist");
            }
        }
        return new CheckingResult(true, "");
    };
    public static final Process NAME_PROCESSING = new Process() {
        @Override
        public String getMessage() {
            return "Enter name: ";
        }

        @Override
        public Correction getCorrection() {
            return NAME_CORRECTION;
        }

        @Override
        public Checker getChecker() {
            return NAME_CHECKER;
        }
    };
    public static final Process X_COORDINATE_PROCESSING = new Process() {
        @Override
        public String getMessage() {
            return "Enter X coordinate: ";
        }

        @Override
        public Correction getCorrection() {
            return NUMBER_VALUE_CORRECTION;
        }

        @Override
        public Checker getChecker() {
            return X_COORDINATE_CHECKER;
        }
    };
    public static final Process Y_COORDINATE_PROCESSING = new Process() {
        @Override
        public String getMessage() {
            return "Enter Y coordinate: ";
        }

        @Override
        public Correction getCorrection() {
            return NUMBER_VALUE_CORRECTION;
        }

        @Override
        public Checker getChecker() {
            return Y_COORDINATE_CHECKER;
        }
    };
    public static final Process ENGINE_POWER_PROCESSING = new Process() {
        @Override
        public String getMessage() {
            return "Enter engine power: ";
        }

        @Override
        public Correction getCorrection() {
            return NUMBER_VALUE_CORRECTION;
        }

        @Override
        public Checker getChecker() {
            return ENGINE_POWER_CHECKER;
        }
    };
    public static final Process DISTANCE_TRAVELLED_PROCESSING = new Process() {
        @Override
        public String getMessage() {
            return "Enter distance travelled: ";
        }

        @Override
        public Correction getCorrection() {
            return NUMBER_VALUE_CORRECTION;
        }

        @Override
        public Checker getChecker() {
            return DISTANCE_TRAVELLED_CHECKER;
        }
    };
    public static final Process VEHICLE_TYPE_PROCESSING = new Process() {
        @Override
        public String getMessage() {
            StringBuilder message = new StringBuilder("Vehicle types:\n");
            for (VehicleType vehicleType : VehicleType.values()) {
                message.append(vehicleType.getSerialNumber()).append(" - ").append(vehicleType).append("\n");
            }
            message.append("Enter vehicle type (numeric value or full name): ");
            return message.toString();
        }

        @Override
        public Correction getCorrection() {
            return TYPE_CORRECTION;
        }

        @Override
        public Checker getChecker() {
            return VEHICLE_TYPE_CHECKER;
        }
    };
    public static final Process FUEL_TYPE_PROCESSING = new Process() {
        @Override
        public String getMessage() {
            StringBuilder message = new StringBuilder("Fuel types:\n");
            for (FuelType fuelType : FuelType.values()) {
                message.append(fuelType.getSerialNumber()).append(" - ").append(fuelType).append("\n");
            }
            message.append("Enter fuel type (numeric value or full name): ");
            return message.toString();
        }

        @Override
        public Correction getCorrection() {
            return TYPE_CORRECTION;
        }

        @Override
        public Checker getChecker() {
            return FUEL_TYPE_CHECKER;
        }
    };

    /**
     * Sequentially performs correction and verification of each newly entered value.
     * @param newValues Mutable values in collection class.commandName
     * @return Exit status of all corrections and verifications.
     */
    public static boolean checkValues(String[] newValues, String commandName) {
        ArrayList<Process> processes = ValueHandler.getValueProcesses();
        boolean exitStatus = true;
        for (int i = 0; i < processes.size(); i++) {
            newValues[i] = processes.get(i).getCorrection().correct(newValues[i]);
            CheckingResult valueCheck = processes.get(i).getChecker().check(newValues[i]);
            if (!valueCheck.getStatus()) {
                if (exitStatus)
                    MessageHolder.putCurrentCommand(commandName, MessageType.USER_ERROR);
                MessageHolder.putMessage(valueCheck.getMessage(), MessageType.USER_ERROR);
                exitStatus = false;
            }
        }
        return exitStatus;
    }

    /**
     * Converts string values to Vehicle class values.
     * @return New assembled collection class.
     */
    public static Vehicle getVehicle(long id, java.time.ZonedDateTime creationDate, String[] newValues) {
        String newName, newX, newY, newEnginePower, newDistanceTravelled, newType, newFuelType;
        newName = newValues[0];
        newX = newValues[1];
        newY = newValues[2];
        newEnginePower = newValues[3];
        newDistanceTravelled = newValues[4];
        newType = newValues[5];
        newFuelType = newValues[6];
        return ValueTransformer.createVehicle(id, newName, newX, newY, creationDate,
                newEnginePower, newDistanceTravelled, newType, newFuelType);
    }
}
