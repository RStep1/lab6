package utility;

/**
 * Allows implementations to validate the fields of a Vehicle class.
 */
public interface Checker {
    CheckingResult check(String value);
}
