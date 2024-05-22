package effectiveMobile.bank.exceptions.handlers;

import effectiveMobile.bank.BankingOperationsServiceApplication;
import effectiveMobile.bank.entities.BankAccount;
import effectiveMobile.bank.exceptions.BankAccountNotFoundException;
import effectiveMobile.bank.exceptions.NotEnoughUnitsException;
import effectiveMobile.bank.exceptions.PersonNotFoundException;
import effectiveMobile.bank.exceptions.ValidationException;
import effectiveMobile.bank.exceptions.dto.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(annotations = RestController.class)
@ResponseBody
public class ExceptionsHandler {

    @ExceptionHandler(PersonNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handlePersonNotFoundException(PersonNotFoundException ex) {
        BankingOperationsServiceApplication.logger.warn("An exception was caused due to the non-presence of a person");

        return new ExceptionDto(ex);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleBadCredentialsException(BadCredentialsException ex) {
        BankingOperationsServiceApplication.logger.warn("An exception was caused due to bad credentials - bad login or password");

        return new ExceptionDto(Collections.singletonMap("authenticationError", "bad login or password"));
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleValidationException(ValidationException ex) {
        Map<String, String> map = new HashMap<>();
        map.put("message", ex.getMessage());

        if (ex.getErrors() != null) {
            ex.getErrors().getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                map.put(fieldName, errorMessage);
            });
        }

        BankingOperationsServiceApplication.logger.warn("An exception was caused due to validations of incoming fields. Messages: {}", map);

        return new ExceptionDto(map);
    }

    @ExceptionHandler(BankAccountNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleBankAccountNotFoundException(BankAccountNotFoundException ex) {
        BankingOperationsServiceApplication.logger.warn("An exception was caused due to the non-presence of a bank account");

        return new ExceptionDto(ex);
    }

    @ExceptionHandler(NotEnoughUnitsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleNotEnoughUnitsException(NotEnoughUnitsException ex) {
        BankingOperationsServiceApplication.logger.warn("A transferring was aborted due to account don't have enough units to transfer");

        return new ExceptionDto(ex);
    }
}