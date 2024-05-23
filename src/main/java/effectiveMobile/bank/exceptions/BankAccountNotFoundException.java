package effectiveMobile.bank.exceptions;

public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException(String msg) {
        super(msg);
    }
    public BankAccountNotFoundException() {
        super("Bank Account Not Found");
    }
    public BankAccountNotFoundException(int id) {
        super("Bank Account with id=" + id + " not found");;
    }
}