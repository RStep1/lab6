package utility;

/**
 * Classes that implement this interface contain
 * messages for output in insert mode, correction and check classes.
 */
public interface Process {
    String getMessage();
    Correction getCorrection();
    Checker getChecker();
}
