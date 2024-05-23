package effectiveMobile.bank.exceptions;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(String message) {
        super(message);
    }
    public PersonNotFoundException() {
        super("person not found");
    }
    public PersonNotFoundException(int id) {
        super ("person with id=" + id + " not found");
    }
}