package effectiveMobile.bank.exceptions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ExceptionDto {
    private Map<String, String> errorMessages;

    public ExceptionDto(Exception ex) {
        errorMessages = Collections.singletonMap("error", ex.getMessage());
    }
}