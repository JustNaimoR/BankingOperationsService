package effectiveMobile.bank.exceptions;

public class IllegalActionException extends RuntimeException{
    public IllegalActionException() {
        super("Illegal action detected");
    }
    public IllegalActionException(String message) {
        super(message);
    }
}