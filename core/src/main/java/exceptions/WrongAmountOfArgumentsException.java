package exceptions;

/**
 * Thrown when the number of arguments does not match the provided.
 */
public class WrongAmountOfArgumentsException extends Exception {
    public WrongAmountOfArgumentsException(String message, int numberOfUserArguments, int expectedNumberOfArguments) {
        super(String.format("%s %s arguments, expected: %s",
                message, numberOfUserArguments, expectedNumberOfArguments));
    }
}
