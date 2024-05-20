package effectiveMobile.bank.exceptions.handlers;

import effectiveMobile.bank.exceptions.PersonNotFoundException;
import effectiveMobile.bank.exceptions.ValidationException;
import effectiveMobile.bank.exceptions.dto.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(annotations = RestController.class)
@ResponseBody
public class ExceptionsHandler {

    @ExceptionHandler(PersonNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handlePersonNotFoundException(PersonNotFoundException ex) {
        return new ExceptionDto(ex);
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

        return new ExceptionDto(map);
    }
}