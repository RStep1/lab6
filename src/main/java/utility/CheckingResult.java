package utility;

/**
 * Acts as a returned value for classes that implement the Checker interface.
 * Contains boolean result and error message.
 */
public class CheckingResult {
    private boolean status;
    private String message;
    public CheckingResult(boolean status, String message) {
        this.status = status;
        this.message = message;
    }
    public boolean getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
}
