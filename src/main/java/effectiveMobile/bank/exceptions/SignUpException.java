package effectiveMobile.bank.exceptions;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Getter
public class SignUpException extends RuntimeException {
    private Errors errors;

    public SignUpException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }
}