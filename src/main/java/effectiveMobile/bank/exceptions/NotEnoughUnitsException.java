package effectiveMobile.bank.exceptions;

public class NotEnoughUnitsException extends RuntimeException {
    public NotEnoughUnitsException() {
        super("Not enough units in account");
    }
}