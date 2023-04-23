package exceptions;

public class NoSuchIdException extends RuntimeException {
    public NoSuchIdException() {}
    public NoSuchIdException(long id) {
        super(String.format("Id = %s: No such id in collection", id));
    }
}
